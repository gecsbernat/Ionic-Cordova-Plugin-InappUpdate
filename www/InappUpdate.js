var exec = require('cordova/exec');

exports.check = function (success, error) {
	exec(success, error, 'InappUpdate', 'check');
};

exports.update = function (arg0, success, error) {
	exec(success, error, 'InappUpdate', 'update', [arg0]);
};