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

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;


/**
 * @author Tijs Rademakers
 */
abstract class DelegatingXmlSerializer implements XmlSerializer {
  
   private final XmlSerializer writer;

   public DelegatingXmlSerializer(XmlSerializer writer) {
       this.writer = writer;
   }

    public void writeStartElement(String localName) {
        try {
            writer.startTag("",localName);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeStartElement(String namespaceURI, String localName)   {
        try {
            writer.startTag(namespaceURI, localName);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI)   {
        try {
            writer.startTag(namespaceURI, localName);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeEmptyElement(String namespaceURI, String localName)   {
        try {
            writer.startTag(namespaceURI, localName);
            writer.endTag(namespaceURI, localName);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI)   {
        try {
            writer.startTag(namespaceURI, localName);
            writer.endTag(namespaceURI, localName);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeEmptyElement(String localName) {
        try {

            writer.startTag("", localName);
            writer.endTag("", localName);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeEndElement()   {
        try {
            writer.endTag("","");
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeEndDocument()   {
        try {
            writer.endDocument();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

   public void close()  {
//       writer.close();
   }

    public void flush()   {
        try {
            writer.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeAttribute(String localName, String value) {
        try {
            writer.attribute("", localName, value);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) {
        try {
            writer.attribute(namespaceURI, localName, value);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeAttribute(String namespaceURI, String localName, String value) {
        try {
            writer.attribute(namespaceURI, localName, value);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void writeNamespace(String prefix, String namespaceURI)   {
        try {
            writer.setPrefix(prefix, namespaceURI);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeDefaultNamespace(String namespaceURI)   {
        try {
            writer.setPrefix("",namespaceURI);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeComment(String data)   {
        try {
            writer.comment(data);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeProcessingInstruction(String target)   {
        try {
            writer.processingInstruction(target);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeProcessingInstruction(String target, String data)   {
        try {
            writer.processingInstruction(data);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } //TODO: Correct if omitting target?
    }

    public void writeCData(String data)   {
        try {
            writer.cdsect(data);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeDTD(String dtd)   {
//       writer.writeDTD(dtd);
        System.err.println(getClass().getCanonicalName() + ": writeDTD commented out.");
    }
    public void writeEntityRef(String name)   {
        try {
            writer.entityRef(name);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeStartDocument()   {
        try {
            writer.startDocument(null, null);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } //TODO: null, null correct?
    }

    public void writeStartDocument(String version)   {
        try {
            if (version.equalsIgnoreCase("1.0")|| version.equalsIgnoreCase("1"))
                writer.startDocument(null, true);
            else
                writer.startDocument(null, false);

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeStartDocument(String encoding, String version)   { //TODO: Is this correct? Version 1 to boolean true
        try {
            if (version.equalsIgnoreCase("1.0")|| version.equalsIgnoreCase("1"))
                writer.startDocument(encoding, true);
            else
                writer.startDocument(encoding, false);

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void writeCharacters(String text) {
        try {
            writer.text(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public void writeCharacters(char[] text, int start, int len) {
       try {
           writer.text(text,start,len);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

    public String getPrefix(String uri)   {
        return writer.getPrefix(uri, true);
    }

    public void setPrefix(String prefix, String uri)   {
        try {
            writer.setPrefix(prefix, uri);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setDefaultNamespace(String uri)   {
        try {
            writer.setPrefix("",uri);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//   public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
//       writer.setNamespaceContext(context);
//   }
//
//   public NamespaceContext getNamespaceContext() {
//       return writer.getNamespaceContext();
//   }

   public Object getProperty(String name) throws IllegalArgumentException {
       return writer.getProperty(name);
   }
}
