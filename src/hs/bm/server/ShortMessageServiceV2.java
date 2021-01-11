package hs.bm.server;



import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class ShortMessageServiceV2
{
	private static ShortMessageServiceV2 sms;

	public static ShortMessageServiceV2 getInstance()
	{
		if (sms == null)
		{
			sms = new ShortMessageServiceV2();
		}
		return sms;
	}
	public String SendMessage(String photo,String data,String templateCode) throws ClientException {
    //设置超时时间-可自行调整
    //System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
    //System.setProperty("sun.net.client.defaultReadTimeout", "10000");
    //初始化ascClient需要的几个参数
    final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
    final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
    //替换成你的AK
    final String accessKeyId = "LTAIsKn6Aq59h2iY";//你的accessKeyId,参考本文档步骤2
    final String accessKeySecret = "3SnSA8mJkgalPRt9tB3lNpbhYaBuvX";//你的accessKeySecret，参考本文档步骤2
    //初始化ascClient,暂时不支持多region（请勿修改）
    IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
    accessKeySecret);
    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
    IAcsClient acsClient = new DefaultAcsClient(profile);
     //组装请求对象
     SendSmsRequest request = new SendSmsRequest();
     //使用post提交
     request.setMethod(MethodType.POST);
     //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
     request.setPhoneNumbers(photo);
     //必填:短信签名-可在短信控制台中找到
     request.setSignName("华通桥涵");
     //必填:短信模板-可在短信控制台中找到
     request.setTemplateCode(templateCode);
     //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
     //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
     request.setTemplateParam(data);
     //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
     //request.setSmsUpExtendCode("90997");
     //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
     //request.setOutId("yourOutId");
    //请求失败这里会抛ClientException异常
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     String nowTime = sdf.format(new Date());
     String hour = nowTime.substring(11, 13);
     SendSmsResponse sendSmsResponse=null;
     if(Integer.parseInt(hour)>=8&&Integer.parseInt(hour)<=18){
    	    sendSmsResponse = acsClient.getAcsResponse(request);
     }
	    if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
	    //请求成功
	    	System.out.println("发送成功");
	    }
	    return sendSmsResponse.getCode();
	}
	/**
	 * 
	 * @param "{\"name\":\"Tom\", \"code\":\"123\"}"
	 */
	public static void main(String[] args) {
		try {
		String code = ShortMessageServiceV2.getInstance().SendMessage("18068800343", "{system:'辉大佬',time:'15小时'}","SMS_105180086");
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
}