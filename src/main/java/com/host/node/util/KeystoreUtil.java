package com.host.node.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class KeystoreUtil {
	
	public static PublicKey publicKey = null;
    
	public static String Cer_File_Path = "/secure.cer";
	public static String classpath = "";
	
	static {
		try {	
			   
			   classpath = KeystoreUtil.class.getResource("/").toString().replace("file:/", "");
			   
			   CertificateFactory certificatefactory=CertificateFactory.getInstance("X.509");
			   FileInputStream bais=new FileInputStream(classpath + Cer_File_Path);
			   X509Certificate cert = (X509Certificate)certificatefactory.generateCertificate(bais);
			   publicKey = cert.getPublicKey();
	           
	           System.out.println("Init public key success");

	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	            e.printStackTrace();
	        }
	}
	
	
	public static String getCommandBySecuredStr(String secureCommandStr) {
		String unSecuredCommandStr = "";
		
		try {
			System.out.println("getCommandBySecuredStr: " + secureCommandStr);
			Node signatureNode = getSignatureNodeFromEncodeFileContent(secureCommandStr);
	        if (signatureNode == null ||
	                !isSignatureValid(getPublicKey(), signatureNode)) {
	        	return unSecuredCommandStr;
	        }
	        
	        Document signedDoc = getXmlDocument(secureCommandStr);
	        unSecuredCommandStr = getNodeContentByTagName(signedDoc, "command");
        
		} catch (Exception e) {
			return unSecuredCommandStr;
		}
		
		return unSecuredCommandStr;
	}
	
    private static PublicKey getPublicKey() {
		return publicKey;
	}

	public static Document getXmlDocument(String commandStr) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            InputStream licenseStrIS = new ByteArrayInputStream(commandStr.getBytes());
            doc = dbf.newDocumentBuilder().parse(licenseStrIS);
            licenseStrIS.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return doc;
    }
    
    public static Node getSignatureNodeFromEncodeFileContent(String licenseFileContent) throws Exception {

        Document signedDoc = getXmlDocument(licenseFileContent);
        NodeList signatureNode = signedDoc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (signatureNode.getLength() == 0) {
            throw new Exception("No XML Digital Signature Found, document is discarded");
        }

        return signatureNode.item(0);
    }

    public static boolean isSignatureValid(PublicKey publicKey, Node signatureNode) throws MarshalException, XMLSignatureException {

        DOMValidateContext valContext = new DOMValidateContext(publicKey, signatureNode);
        XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
        XMLSignature signature = factory.unmarshalXMLSignature(valContext);
        return signature.validate(valContext);
    }

    public static String getNodeTextContent(NodeList nodelist) {
        String textContent = "";
        if (nodelist != null && nodelist.item(0) != null) {
            textContent = nodelist.item(0).getTextContent();
        }
        return textContent.trim();
    }

    public static String getNodeContentByTagName(Document signedDoc, String tagName) {
        NodeList nodelist = signedDoc.getElementsByTagName(tagName);
        return getNodeTextContent(nodelist);
    }
}
