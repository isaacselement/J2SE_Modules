package com.modules.Helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class ByteHelper {

	public static byte[] int2byte(int res) {
		byte[] targets = new byte[4];

		targets[0] = (byte) (res & 0xff);// 最低位
		targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
		targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
		targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
		return targets;
	}

	public static int byte2int(byte[] res) {
		// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000

		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) | ((res[2] << 24) >>> 8) | (res[3] << 24);
		return targets;
	}

	public static byte[] intToByteArray(int i) throws Exception {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buf);
		System.out.println("i:" + i);
		out.writeInt(i);
		byte[] b = buf.toByteArray();
		System.out.println("i:" + b);
		out.close();
		buf.close();
		return b;
	}

	public static int ByteArrayToInt(byte b[]) throws Exception {
//		int temp = 0, a = 0;
		ByteArrayInputStream buf = new ByteArrayInputStream(b);

		return buf.read();
		// while (buf.available() > 0) {
		// System.out.println(a);
		//
		// System.out.println(a);
		// temp = a ;
		// }
		// return temp;
		// while ((System.out.println(a);)!=-1)
		// {
		// System.out.println(a);
		// temp = a;
		//
		// }
		// return temp;

	}
}