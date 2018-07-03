'use strict';

import { NativeModules } from 'react-native';

const RNPinch = {
   fetch: async (url, obj) => {
       try{
           const res = await NativeModules.RNPinch.fetch(url, obj);
           return {
               json : async()=> {
                   return JSON.parse(res.bodyString);
                 
               },
               text : async () =>{
                       return res.bodyString;
               },
               url
           }
       }catch(e){
           throw e
       }
   }
};

module.exports =  RNPinch;