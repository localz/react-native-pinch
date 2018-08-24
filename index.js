'use strict';

import { NativeModules } from 'react-native';

const RNPinch = {
  fetch: async (url, obj) => {
    const {
      status,
      errorCode,
      errorMessage,
      headers,
      ...res
    } = await NativeModules.RNPinch.fetch(url, obj);
    return {
      json: async () => {
        if (errorCode) {
          throw { code: parseInt(errorCode), message: errorMessage };
        }
        return JSON.parse(res.bodyString);
      },
      text: async () => {
        if (errorCode) {
          throw { code: parseInt(errorCode), message: errorMessage };
        }
        return res.bodyString;
      },
      url,
      status,
      headers
    };
  }
};

module.exports = RNPinch;
