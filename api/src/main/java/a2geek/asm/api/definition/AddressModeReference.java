package a2geek.asm.api.definition;

import jakarta.xml.bind.annotation.XmlType;

/**
 * This is a place-holder which we presume will be inherited from another CPU definition.
 * If the reference is not inherited, that implies a structural error in the definition.
 * Within the XML file, the reference serves as a contract that the addressing mode
 * will be defined.
 *  
 * @author Rob
 */
@XmlType(name = "AddressModeReferenceType")
public class AddressModeReference extends AddressMode {
}
