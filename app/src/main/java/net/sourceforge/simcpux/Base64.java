package net.sourceforge.simcpux;

public class Base64 {
	// 字符的顺序调整
	static byte[] base64char = "l012345abcdefghijk6789mnopqrstQRSTUVWXYZABCDEuvwxyzFGHIJKLMNOP+/".getBytes();

	// 模仿base64,加密函数
	public static String encode(byte[] str, int length) {
		int i, j;
		int current;
		int size = 4 * ((length + 2) / 3 + 1);
		byte[] base64 = new byte[size];

		for (i = 0, j = 0; i < length; i += 3) {
			// first 6 bits
			current = (str[i] >> 2);
			current &= 0x3F;
			base64[j++] = base64char[current];

			// second 6 bits
			current = ((str[i] << 4)) & (0x30);
			if (i + 1 >= length) {
				base64[j++] = base64char[(int) current];
				base64[j++] = '=';
				base64[j++] = '=';
				break;
			}
			current |= ((str[i + 1] >> 4)) & (0x0F);
			base64[j++] = base64char[(int) current];

			// third 6 bits
			current = ((str[i + 1] << 2)) & (0x3C);
			if (i + 2 >= length) {
				base64[j++] = base64char[(int) current];
				base64[j++] = '=';
				break;
			}
			current |= ((str[i + 2] >> 6)) & (0x03);
			base64[j++] = base64char[(int) current];

			// fourth 6 bits
			current = (str[i + 2]) & (0x3F);
			base64[j++] = base64char[(int) current];
		}
		base64[j] = '\0';
		String url = new String(base64).trim();
		return url;
	}

	// 模仿base64,解密函数
	public static byte[] decode(byte[] str, int length) {
		int i, j;
		byte k;
		byte[] temp = new byte[4];
		byte[] bindata = new byte[length];

		for (i = 0, j = 0; str[i] != '\0'; i += 4) {
			for (k = 0; k < 64; k++) {
				if (base64char[k] == str[i])
					temp[0] = k;
			}

			for (k = 0; k < 64; k++) {
				if (base64char[k] == str[i + 1])
					temp[1] = k;
			}

			for (k = 0; k < 64; k++) {
				if (base64char[k] == str[i + 2])
					temp[2] = k;
			}

			for (k = 0; k < 64; k++) {
				if (base64char[k] == str[i + 3])
					temp[3] = k;
			}

			bindata[j++] = (byte) (((((temp[0] << 2)) & 0xFC)) | (((temp[1] >> 4) & 0x03)));
			if (str[i + 2] == '=')
				break;

			bindata[j++] = (byte) (((((temp[1] << 4)) & 0xF0)) | (((temp[2] >> 2) & 0x0F)));
			if (str[i + 3] == '=')
				break;

			bindata[j++] = (byte) (((((temp[2] << 6)) & 0xF0)) | ((temp[3] & 0x3F)));
		}

		return bindata;
	}

	public static void main(String[] args) {
		int length;
		byte[] encodestr;
		byte[] decodestr;
		byte[] data = "https://www.baidu.com".getBytes();
		length = data.length;

	}
}
