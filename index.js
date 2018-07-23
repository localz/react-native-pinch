'use strict';

import { NativeModules } from 'react-native';
var Q = require('q');

var RNPinch = {
    fetch: function (url, obj, callback) {
        var deferred = Q.defer();
        NativeModules.RNPinch.fetch(url, obj, (err, res) => {

            if (err) {
                deferred.reject(err);
            } else {
                res.json = function() {
                    return Q.fcall(function () {
                        return JSON.parse(res.bodyString);
                    });
                };
                res.text = function() {
                    return Q.fcall(function () {
                        return res.bodyString;
                    });
                };
                res.url = url;

                deferred.resolve(res);
            }

            deferred.promise.nodeify(callback);
        });
        return deferred.promise;
    }
};

module.exports =  RNPinch;
