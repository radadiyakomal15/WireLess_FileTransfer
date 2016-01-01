
public class WirelessTest {

	public static void main(String[] args) {
		
		String file_list_tobe_sent_to_server = "[dgfdghkhfg]";

		String filename1 = file_list_tobe_sent_to_server.toString().substring(1);
		String filename2 = filename1.toString().substring(0,filename1.length()-1);
		System.out.println(filename2);
	}

}
