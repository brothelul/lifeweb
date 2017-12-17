package lifeweb;

import com.brotherlu.lifeweb.utils.Md5Util;

public class TestMD5 {
	public static void main(String[] args) {
		String password = Md5Util.pwdDigest("112358");
		System.out.println(password);
	}
}
//5E1EC19BB56B0F099E47F928AAF74264
//5E1EC19BB56B0F099E47F928AAF74264