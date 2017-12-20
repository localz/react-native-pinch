//
//  RNNativeFetch.h
//  medipass
//
//  Created by Paul Wong on 13/10/16.
//  Copyright © 2016 Localz. All rights reserved.
//

#import <Foundation/Foundation.h>
#if __has_include(<React/RCTBridgeModule.h>)
#import "React/RCTBridgeModule.h"
#import "React/RCTLog.h"
#else
#import "RCTBridgeModule.h"
#import "RCTLog.h"
#endif

@interface RNPinch : NSObject <RCTBridgeModule>

@end
