/*
 * Plugin Custom
 */
"use strict";

$$.custom = $$.custom || {};

$$.custom.bitel = new function() {
	const debug = $$.debug("custom.bitel");
	
	/**
	 * A demo function, may be called as $$.custom.bitel.demo().
	 * @param {*} param 
	 */
	this.demo = (param) => {
		// TODO: make logic
	}

	// public functions
	this.demo = demo;
}
