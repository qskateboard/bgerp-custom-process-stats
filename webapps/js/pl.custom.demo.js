/*
 * Plugin Custom Demo
 */
"use strict";

$$.custom = $$.custom || {};

$$.custom.demo = new function() {
	const debug = $$.debug("custom");

	/**
	 * A demo function, may be called as $$.custom.demo.demo().
	 * @param {*} param
	 */
	const demo = (param) => {
		// TODO: make logic
	}

	// public functions
	this.demo = demo;
}
