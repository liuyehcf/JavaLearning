package org.liuyehcf.aliyun.kms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.kms.model.v20160120.*;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import static org.liuyehcf.aliyun.AccessUtils.ACCESS_KEY;
import static org.liuyehcf.aliyun.AccessUtils.ACCESS_SECRET_KEY;

public class KmsDemo {
    static DefaultAcsClient kmsClient;

    private static DefaultAcsClient kmsClient(String regionId, String accessKeyId, String accessKeySecret) {
        /**
         * Construct an Aliyun Client:
         * Set RegionId, AccessKeyId and AccessKeySecret
         */
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        return client;
    }

    private static CreateKeyResponse CreateKey(String keyDesc, String keyUsage) throws ClientException {
        final CreateKeyRequest ckReq = new CreateKeyRequest();
        ckReq.setProtocol(ProtocolType.HTTPS);
        ckReq.setAcceptFormat(FormatType.JSON);
        ckReq.setMethod(MethodType.POST);
        ckReq.setDescription(keyDesc);
        ckReq.setKeyUsage(keyUsage);
        final CreateKeyResponse response = kmsClient.getAcsResponse(ckReq);
        return response;
    }

    private static DescribeKeyResponse DescribeKey(String keyId) throws ClientException {
        final DescribeKeyRequest decKeyReq = new DescribeKeyRequest();
        decKeyReq.setProtocol(ProtocolType.HTTPS);
        decKeyReq.setAcceptFormat(FormatType.JSON);
        decKeyReq.setMethod(MethodType.POST);
        decKeyReq.setKeyId(keyId);
        final DescribeKeyResponse decKeyRes = kmsClient.getAcsResponse(decKeyReq);
        return decKeyRes;
    }

    private static ListKeysResponse ListKey(int pageNumber, int pageSize) throws ClientException {
        final ListKeysRequest listKeysReq = new ListKeysRequest();
        listKeysReq.setProtocol(ProtocolType.HTTPS);
        listKeysReq.setAcceptFormat(FormatType.JSON);
        listKeysReq.setMethod(MethodType.POST);
        listKeysReq.setPageNumber(pageNumber);
        listKeysReq.setPageSize(pageSize);
        final ListKeysResponse listKeysRes = kmsClient.getAcsResponse(listKeysReq);
        return listKeysRes;
    }

    private static GenerateDataKeyResponse GenerateDataKey(String keyId, String keyDesc, int numOfBytes) throws ClientException {
        final GenerateDataKeyRequest genDKReq = new GenerateDataKeyRequest();
        genDKReq.setProtocol(ProtocolType.HTTPS);
        genDKReq.setAcceptFormat(FormatType.JSON);
        genDKReq.setMethod(MethodType.POST);
        /**
         * Set parameter according to KMS openAPI document:
         * 1.KeyId
         * 2.KeyDescription
         * 3.NumberOfBytes
         */
        genDKReq.setKeySpec(keyDesc);
        genDKReq.setKeyId(keyId);
        genDKReq.setNumberOfBytes(numOfBytes);
        final GenerateDataKeyResponse genDKRes = kmsClient.getAcsResponse(genDKReq);
        return genDKRes;
    }

    private static EncryptResponse Encrypt(String keyId, String plainText) throws ClientException {
        final EncryptRequest encReq = new EncryptRequest();
        encReq.setProtocol(ProtocolType.HTTPS);
        encReq.setAcceptFormat(FormatType.JSON);
        encReq.setMethod(MethodType.POST);
        encReq.setKeyId(keyId);
        encReq.setPlaintext(plainText);
        final EncryptResponse encResponse = kmsClient.getAcsResponse(encReq);
        return encResponse;
    }

    private static DecryptResponse Decrypt(String cipherBlob) throws ClientException {
        final DecryptRequest decReq = new DecryptRequest();
        decReq.setProtocol(ProtocolType.HTTPS);
        decReq.setAcceptFormat(FormatType.JSON);
        decReq.setMethod(MethodType.POST);
        decReq.setCiphertextBlob(cipherBlob);
        final DecryptResponse decResponse = kmsClient.getAcsResponse(decReq);
        return decResponse;
    }

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("Getting Started with KMS Service");
        System.out.println("===========================================\n");


        String regionId = "cn-hangzhou";
        String accessKeyId = ACCESS_KEY;
        String accessKeySecret = ACCESS_SECRET_KEY;
        kmsClient = kmsClient(regionId, accessKeyId, accessKeySecret);
        String keyId = "fb9b6177-93fa-4d63-8e6c-2c7c3cc70c9c";
        String plainText = "MY_PASSOWRD";
        String cipherBlob = null;


        /**
         * Encrypt the plain text and got a cipher one
         */
        try {
            EncryptResponse encResponse = Encrypt(keyId, plainText);
            cipherBlob = encResponse.getCiphertextBlob();
            System.out.println("CiphertextBlob: " + cipherBlob);
            System.out.println("KeyId: " + encResponse.getKeyId());
            System.out.println("===========================================");
            System.out.println("Encrypt the plain text success!");
            System.out.println("===========================================\n");
        } catch (ClientException eResponse) {
            System.out.println("Failed.");
            System.out.println("Error code: " + eResponse.getErrCode());
            System.out.println("Error message: " + eResponse.getErrMsg());
        }
        /**
         * Decrypt the cipher text and verify result with original plain text.
         */
        try {
            DecryptResponse decResponse = Decrypt(cipherBlob);
            System.out.println("Plaintext: " + decResponse.getPlaintext());
            String verifyPlainText = decResponse.getPlaintext();
            int isMatch = verifyPlainText.compareTo(plainText);
            System.out.println("KeyId: " + decResponse.getKeyId());
            System.out.println("===========================================");
            System.out.printf("Decrypt the cipher text success, result " + (isMatch == 0 ? "match" : "mismatch" + "\n"));
            System.out.println("===========================================\n");
        } catch (ClientException eResponse) {
            System.out.println("Failed.");
            System.out.println("Error code: " + eResponse.getErrCode());
            System.out.println("Error message: " + eResponse.getErrMsg());
        }
    }
}
