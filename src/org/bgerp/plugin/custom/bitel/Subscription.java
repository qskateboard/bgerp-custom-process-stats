package org.bgerp.plugin.custom.bitel;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bgerp.util.Log;

import ru.bgcrm.cache.Cache;
import ru.bgcrm.cache.CacheHolder;
import ru.bgcrm.cache.ParameterCache;
import ru.bgcrm.dao.ParamValueDAO;
import ru.bgcrm.dao.process.Tables;
import ru.bgcrm.model.param.ParameterListCountValue;
import ru.bgcrm.util.Setup;
import ru.bgcrm.util.Utils;

/**
 * Calculator and constants for subscriptions.
 * @author Shamil Vakhitov
 */
public class Subscription extends Cache<Subscription> {
    private static final Log log = Log.getLog();

    public static final int BILLING_PERIOD_DAYS = 30;
    public static final int BILLING_INFORM_BEFORE_DAYS = 10;

    /** Message type ID with class MessageTypeEmail. */
    public static final int MESSAGE_TYPE_EMAIL_ID = 5;

    /** Language, type 'list'. Value's titles must be defined as: 'RU', 'EN', 'DE'.*/
    public static final int PARAM_LANG_ID = 49;
    private static final int PARAM_LANG_DEFAULT_VALUE_ID = 1;

    /** Contact E-Mail, type 'email' */
    public static final int PARAM_EMAIL_ID = 53;

    /** Subscription mode, type 'list' */
    public static final int PARAM_CURRENCY_ID = 52;

    /** Sessions limit, type 'list'. */
    public static final int PARAM_SESSIONS_ID = 54;

    /** Plugin prices, type 'listcount' */
    public static final int PARAM_PRICE_RUB_ID = 50;
    public static final int PARAM_PRICE_EUR_ID = 51;

    /** Process type ID for Plugin. */
    public static final int PROCESS_TYPE_PLUGIN_ID = 14;

    public static final int PROCESS_STATUS_OPEN_ID = 1;
    public static final int PROCESS_STATUS_SUPPORT_ID = 10;

    /** Process type ID for Subscription. */
    public static final int PROCESS_TYPE_SUBSCRIPTION_ID = 16;

    private static CacheHolder<Subscription> holder = new CacheHolder<>(new Subscription());

    /**
     * Find a language's parameter ID by title.
     * @param lang title: 'ru', 'en'.
     * @return
     */
    public static final int langToId(String lang) {
        var found = ParameterCache.getListParamValues(PARAM_LANG_ID).stream()
            .filter(v -> lang.equalsIgnoreCase(v.getTitle()))
            .findFirst();
        return found.isPresent() ? found.get().getId() : PARAM_LANG_DEFAULT_VALUE_ID;
    }

    /**
     * Get subscription's cost.
     * @param currencyId currency ID = param ID.
     * @param sessionsId sessions limit ID.
     * @param processIds plugin processes IDs.
     * @return
     */
    public static BigDecimal getCost(int currencyId, int sessionsId, Collection<Integer> processIds) {
        var result = BigDecimal.ZERO;

        var cacheInstance = holder.getInstance();

        log.debug("getCost currencyId: %s, sessionsId: %s", currencyId, sessionsId);

        for (int processId : processIds) {
            var key = new SubscriptionKey(processId, currencyId);

            var prices = cacheInstance.prices.get(key);
            if (prices == null) continue;

            var price = prices.get(sessionsId);
            if (price == null) continue;

            result = result.add(price.getCount());

            log.debug("add processId: %s, add: %s", processId, price.getCount());
        }

        return result;
    }

    public static void flush() {
        holder.flush(null);
    }

    // END OF STATIC

    private Map<SubscriptionKey, Map<Integer, ParameterListCountValue>> prices;

    private Subscription() {}

    protected Subscription newInstance() {
        var instance = new Subscription();

        instance.prices = new HashMap<>(100);

        try (var con = Setup.getSetup().getDBSlaveConnectionFromPool()) {
            var paramDao = new ParamValueDAO(con);

            var query =
                "SELECT id FROM " + Tables.TABLE_PROCESS + " AS p " +
                "WHERE type_id=? AND status_id IN (" + Utils.toString(List.of(PROCESS_STATUS_OPEN_ID, PROCESS_STATUS_SUPPORT_ID)) + ")";
            var ps = con.prepareStatement(query);
            ps.setInt(1, PROCESS_TYPE_PLUGIN_ID);
            var rs = ps.executeQuery();
            while (rs.next()) {
                int processId = rs.getInt(1);
                loadPrices(instance, paramDao, processId, PARAM_PRICE_RUB_ID);
                loadPrices(instance, paramDao, processId, PARAM_PRICE_EUR_ID);
            }
            ps.close();
        } catch (Exception e) {
            log.error(e);
        }

        return instance;
    }

    private void loadPrices(Subscription instance, ParamValueDAO paramDao, int processId, int paramId) throws Exception {
        var key = new SubscriptionKey(processId, paramId);
        var prices = paramDao.getParamListCount(processId, paramId);
        instance.prices.put(key, prices);
    }
}
