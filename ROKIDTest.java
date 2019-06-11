package com.nio.ROKID;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.nio.utils.MD5;

public class ROKIDTest {
	
	public static String url = "https://mapi.open.rokid.com/rest/asr/8888";
	
	public static String PersonID = "F53EE37779B04C9AACAA8ADDCAF0751C";
	public static String TypeID = "B3B9D2585DFE4AE5A5E49309DD005433";
	public static String key = "B958552BE0DC4D0BBCE95A1D5C43B3B6";
	public static String secret = "16DD9DA20BC14F35A065BBCC3055A300";
	public static String SN = "72D15522AB9F4AF";
	public static String version = "2";
	
	public static String getResponse(String file) throws Exception{
		String path = ROKIDTest.class.getClassLoader().getResource(file).getPath();
		//file = "/Users/yanhui.li/Desktop/program/git/inhouse_asr_test/src/main/java/com/nio/ROKID/rqjttqzmy.pcm";
		Map<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("voice", FileUtils.readFileToByteArray(new File(path)));
		paramsMap.put("id", "0");
		paramsMap.put("type", "START");
		
		Map<String, String> headersMap = getAsrHeaders();
		for(Entry<String, String> each : headersMap.entrySet()) {
			System.out.println(each.getKey() + ":" + each.getValue());
		}
		
		try {
			String response = HttpsUtil2.doPost(url, paramsMap, headersMap);
			System.out.println("response:" + response);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	    
	}	
	
	public static Map<String, String> getAsrHeaders() {
	    Map<String, String> headMap = new HashMap<>();
	    String time = String.valueOf(System.currentTimeMillis());
	    String str = String
	        .format("key=%s&device_type_id=%s&device_id=%s&service=%s&version=%s&time=%s&secret=%s",
	            key, TypeID, SN, "asr", version, time, secret);
	    String sign = MD5.string2MD5(str);
	    String Authorization = String.format("key=%s;device_type_id=%s;device_id=%s;service=%s;version=%s;time=%s;sign=%s", key, TypeID, SN, "asr", version, time, sign);
	    headMap.put("Authorization",Authorization);
	    String voiceConfig = String.format("codec=%s;voice_trigger=%s;engine=%s;need_hotWords=%s","PCM", "若琪", "ZH", "false");
	    headMap.put("voice-config", voiceConfig);
	    return headMap;
	  }

	public static void main(String[] args) throws Exception{
		getResponse("rqjttqzmy.pcm");
	}

}
