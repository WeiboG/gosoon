package com.gosoon.util;

public class programSetting {

	public static final boolean debug = true;
	public static final int itemPerPage = 5; 
	public static final String APPURL = "http://www.gosoon60.com/data/gosoon.apk";

	static final String REQUEST_URL = "http://www.gosoon60.com/";   
	static final String ADS_URL = "http://www.gosoon60.com/adbanner/v2/appadd.json";
	static final String APPVERSION_URL = "http://www.gosoon60.com/appversion.json";
	static final String ADS_IMAGE_URL = "http://www.gosoon60.com/";
	
	
	 /* 本地服务器测试网址*/
	
	/*static final String REQUEST_URL = "http://192.168.1.10/gosoon60.com/";
	static final String ADS_URL = "http://192.168.1.10/gosoon60.com/adbanner/v2/appadd.json";
	static final String APPVERSION_URL = "http://192.168.1.10/gosoon60.com/appversion.json";
	static final String ADS_IMAGE_URL = "http://192.168.1.10/gosoon60.com/";*/
	
	static final String PARTNER = "2088711957653400";
	static final String SELLER = "2978674701@qq.com";
	
	static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMbBGcdJjhARsr74"
			+ "ZLjow38CvpFx1yCEo+6JasCTr/nT4u7/dMCsKTg8cx9IsX9rCArMPEP/KYLMnEm+"
			+ "bh9dq7pwgYJWuX0Wso3vJIKCGgvcN7QPhfivWQ9xIRTQFTKadsgBSShRBx8BFPKP"
			+ "+M3NwVPDfSo6LkcwUVK9LQAVRYbhAgMBAAECgYEAgaUh6JG9PhQdUV57bPY/11B1"
			+ "+C+W0RpUSFuQq89qQX8KDzXI9YkXVhxwR57GUUQCufBg+0Ws0SogZCX4dTNHuS3d"
			+ "wgohVaTbcizg8OJ+SbxTWRKBXeifnxvNG00yQnK+oOmp3YVYTBDEXadFqCNu/bOX"
			+ "zubmTwVxzvo276DrqEUCQQDst2z+C8CfQe2ZOXK5mF3QUAHubj2tFY5Mcxc8TYQL"
			+ "esKMLbFgjDD9iYEi4H87Zf/GccwrjtMaRItWImxzqzmLAkEA1vH/NrpZZgl4G/q0"
			+ "c2v1WmzPgOMgIsD52AUxkrd5KNhYHsRo1E/H0bTPCJ/9mfYXthWTImtdz4NXcsqJ"
			+ "hgxWwwJBALyyEw4ILEpHcbFc3BkaXl46CPAZrMyWjFJgxDrrx8Cm4QKBQg4mcr8I"
			+ "p4o3zvR4gIWeHRTzy0/7J99HnZAvA7UCQQCfjNx7vFG12xuL8UnXT3C9YX2Z1344"
			+ "LfSHNDu7A3PtLMIkfHiv+FSDxmnTpibyDOlG4Lbp2ra03XKhI9R3a1tPAkBCqv6U"
			+ "vdhZCamzbeJ3V4dX3k9o3CkssKeYOF0odlLJIJ5Y6tJWWgUL16zTGeN2YkY6i/hk"
			+ "cVcSeDcTkZuYsoOU";
	static final double FREE_SHIPPING_LIMIT = 50;
	static final double SHIPPING_FEE = 6;

	static final String RELEASE_REQUEST_URL = REQUEST_URL;
	static final String RELEASE_ADS_URL = ADS_URL;
	static final String RELEASE_APPVERSION_URL = APPVERSION_URL;
	static final String RELEASE_ADS_IMAGE_URL = ADS_IMAGE_URL;
	static final String RELEASE_PARTNER = PARTNER;
	static final String RELEASE_SELLER = SELLER;
	static final String RELEASE_RSA_PRIVATE = RSA_PRIVATE; 
	public static String getRequestUrl() {
		if (debug) {
			return REQUEST_URL;
		}
		return RELEASE_REQUEST_URL;
	}
	public static String getADSUrl() {
		if (debug) {
			return ADS_URL;
		}
		return RELEASE_ADS_URL;
	}
	public static String getAppVersionUrl() {
		if (debug) {
			return APPVERSION_URL;
		}
		return RELEASE_APPVERSION_URL;
	}
	public static String getADSImageUrl() {
		if (debug) {
			return ADS_IMAGE_URL;
		}
		return RELEASE_ADS_IMAGE_URL;
	}
	public static String getPartner() {
		if (debug) {
			return PARTNER;
		}
		return RELEASE_PARTNER;
	}
	public static String getSeller() {
		if (debug) {
			return SELLER;
		}
		return RELEASE_SELLER;
	}
	public static String getRSAPrivate() {
		if (debug) {
			return RSA_PRIVATE;
		}
		return RELEASE_RSA_PRIVATE;
	}
	public static double getFreeShippingLimit() {
		return FREE_SHIPPING_LIMIT;
	}
	public static double getShippingFee() {
		return SHIPPING_FEE;
	}
}
