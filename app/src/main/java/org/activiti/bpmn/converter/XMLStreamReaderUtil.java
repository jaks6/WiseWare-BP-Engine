/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.bpmn.converter;

//import  javax.xml.stream.XMLStreamConstants;
import org.xmlpull.v1.XmlPullParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tijs Rademakers
 */
public class XMLStreamReaderUtil {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(XMLStreamReaderUtil.class);

  public static String moveDown(XmlPullParser xtr) {
    try {
      int eventType = xtr.getEventType();

      while (eventType != XmlPullParser.END_DOCUMENT) {
        int event = xtr.next();
        switch ( event ) {
          case XmlPullParser.END_DOCUMENT:
            return null;
          case XmlPullParser.START_TAG:
            return xtr.getName();
          case XmlPullParser.END_TAG:
            return null;
        }
      }
    } catch (Exception e) {
      LOGGER.warn("Error while moving down in XML document", e);
    }
    return null;
  }
  
  public static boolean moveToEndOfElement(XmlPullParser xtr, String elementName ) {
    try {
      int eventType = xtr.getEventType();

      while (eventType != XmlPullParser.END_DOCUMENT) {
        switch ( eventType ) {
          case XmlPullParser.END_DOCUMENT:
            return false;
          case XmlPullParser.END_TAG:
            if (xtr.getName().equals(elementName))
              return true;
          break;
        }
      }
    } catch (Exception e) {
      LOGGER.warn("Error while moving to end of element {}", elementName, e);
    }
    return false;
  }
}
