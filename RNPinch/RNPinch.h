//
//  RNNativeFetch.h
//  medipass
//
//  Created by Paul Wong on 13/10/16.
//  Copyright © 2016 Localz. All rights reserved.
//

#import <Foundation/Foundation.h>
#if __has_include(<React/RCTBridgeModule.h>)
  #import <React/RCTBridgeModule.h>
#else
  #import "RCTBridgeModule.h"
#endif
#import "RCTLog.h"

@interface RNPinch : NSObject <RCTBridgeModule>

@end
