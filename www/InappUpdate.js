var exec = require('cordova/exec');
module.exports = {
	check: function (success, error) {
		exec(success, error, 'InappUpdate', 'check');
	},
	update: function (success, error) {
		exec(success, error, 'InappUpdate', 'update');
	}
};