///*
// *    Copyright 2009-2012 The MyBatis Team
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//package org.apache.ibatis.logging.log4j;
//
//import org.apache.ibatis.logging.Log;
//import org.apache.log4j.Logger;
//
//public class Log4jImpl implements Log {
//
//  private Logger log;
//
//  public Log4jImpl(String clazz) {
//    log = Logger.getLogger(clazz);
//  }
//
//  public boolean isDebugEnabled() {
//    return log.isDebugEnabled();
//  }
//
//  public boolean isTraceEnabled() {
//    return log.isTraceEnabled();
//  }
//
//  public void error(String s, Throwable e) {
//    android.util.Log.e(getClass().getCanonicalName(), s, e);
//  }
//
//  public void error(String s) {
//    android.util.Log.e(getClass().getCanonicalName(), s);
//  }
//
//  public void debug(String s) {
//    log.debug(s);
//  }
//
//  public void trace(String s) {
//    log.trace(s);
//  }
//
//  public void warn(String s) {
//    android.util.Log.w(s);
//  }
//
//}
