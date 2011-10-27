package org.switchyard.validate.xml;

import java.io.IOException;
import java.io.Reader;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.switchyard.Message;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.Scannable;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.validate.BaseValidator;
import org.switchyard.validate.config.model.XmlSchemaType;
import org.switchyard.validate.config.model.XmlValidateModel;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML Validator {@link org.switchyard.validate.Validator}.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@Scannable(false)
public class XmlValidator extends BaseValidator<Message> {

    private static final Logger LOGGER = Logger.getLogger(XmlValidator.class);
    private XmlSchemaType _schemaType;
    private String _schemaTypeUri;
    private String _schemaFile;
    private boolean _failOnWarning;
        
    /**
     * constructor.
     * @param name name
     * @param model model
     */
    public XmlValidator(QName name, XmlValidateModel model) {
        super(name);

        _schemaType = model.getSchemaType();
        if (_schemaType == null) {
            throw new SwitchYardException("Could not instantiate XmlValidator: schemaType must be specified.");
        }
        
        switch(_schemaType) {
        case DTD:
            _schemaTypeUri = XMLConstants.XML_DTD_NS_URI;
            break;
        case XML_SCHEMA:
            _schemaTypeUri = XMLConstants.W3C_XML_SCHEMA_NS_URI;
            break;
        case RELAX_NG:
            _schemaTypeUri = XMLConstants.RELAXNG_NS_URI;
            break;
        default:
            throw new SwitchYardException("Could not instantiate XmlValidator: schemaType '" + _schemaType + "' is invalid."
                    + "It must be the one of " + XmlSchemaType.values() + ".");
        }
        
        _schemaFile = model.getSchemaFile();
        _failOnWarning = model.failOnWarning();
        
    }
    
    @Override
    public boolean validate(Message msg) {
        if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(_schemaTypeUri) || XMLConstants.RELAXNG_NS_URI.equals(_schemaTypeUri)) {
            // XML Schema or RELAX NG Validation needs schemaFile
            if (_schemaFile == null) {
                throw new SwitchYardException("Error during validation: schemaFile must be specified for '" + _schemaType + "'.");
            }

            SchemaFactory schemaFactory = SchemaFactory.newInstance(_schemaTypeUri);
            try {
                Schema schema = schemaFactory.newSchema(new StreamSource(Classes.getResourceAsStream(_schemaFile)));
                Validator validator = schema.newValidator();
                validator.setErrorHandler(new XmlValidationErrorHandler(_failOnWarning));
                validator.validate(new StreamSource(msg.getContent(Reader.class)));
            } catch (SAXException e) {
                throw new SwitchYardException("Error during validation with '" + _schemaFile + "' as '" + _schemaType + "'.", e);
            } catch (IOException ioe) {
                throw new SwitchYardException("Error during validation with '" + _schemaFile + "' as '" + _schemaType + "'.", ioe);
            }
        } else if (XMLConstants.XML_DTD_NS_URI.equals(_schemaTypeUri)) {
            // DTD Validation
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            try {
                factory.newSAXParser()
                        .parse(new InputSource(msg.getContent(Reader.class)), new XmlValidationErrorHandler(_failOnWarning));
            } catch (Exception e) {
                throw new SwitchYardException("Error during validation with '" + _schemaFile + "' as '" + _schemaType + "'.", e);
            }
            
        } else {
            throw new SwitchYardException("Unknown XML Schema type '" + _schemaType + "', should be one of '" + XmlSchemaType.values() + "'.");
        }
        
        return true;
    }

    private class XmlValidationErrorHandler extends DefaultHandler {
        private boolean _failOnWarning;

        public XmlValidationErrorHandler(boolean failOnWarning) {
            _failOnWarning = failOnWarning;
        }
        
        @Override
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }
        
        @Override
        public void warning(SAXParseException e) throws SAXException {
            if (_failOnWarning) {
                throw e;
            } else {
                LOGGER.warn("Warning during validation with '" + _schemaFile + "' as '" + _schemaType + "'", e);
            }
        }
    }
}
