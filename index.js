import { NativeModules } from 'react-native';
const Pinch = NativeModules.RNPinch;

class ResponseError extends Error {
  name = 'ResponseError';
  constructor(message, code = 8000) {
    super(message);
    this.message = message;
    this.code = parseInt(code, 10);
  }
}

export default class Fetch {
  static async fetch(url, options) {
    const {
      status,
      errorCode,
      errorMessage,
      headers,
      ...res
    } = await Pinch.fetch(url, options);
    return {
      json: async () => {
        if (errorCode) {
          throw new ResponseError(errorMessage, errorCode);
        }
        try {
          return JSON.parse(res.bodyString);
        } catch (e) {
          throw new ResponseError(res.bodyString, e.code);
        }
      },
      text: async () => {
        if (errorCode) {
          throw new ResponseError(errorMessage, errorCode);
        }
        return res.bodyString;
      },
      url,
      status,
      headers
    };
  }
}
