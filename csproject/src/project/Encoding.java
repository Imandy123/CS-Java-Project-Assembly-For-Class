package project;

public class Encoding {
		private boolean noArg;
		private byte opcode;
		private int arg;
		private static int numNoArg;
		public Encoding(boolean nArg, byte opcd, int rg) {
			super();
			this.noArg = nArg;
			this.opcode = opcd;
			this.arg = rg;
			if (noArg == true) {
				numNoArg = numNoArg + 1;
			}
		}
		public boolean isNoArg() {
			return noArg;
		}
		public byte getOpcode() {
			return opcode;
		}
		public int getArg() {
			return arg;
		}
		public static int getNumNoArg() {
			return numNoArg;
		}
		public String toString() {
			if(noArg) return "[" + opcode + "]";
			return "[" + opcode + ", " + arg + "]";
		}
		public static int resetNumNoArg() {
			return numNoArg = 0;
		}
		public long asLong() {
			long longOp = opcode;
			long longArg = arg;
			longOp = longOp << 32;
			longArg = longArg & 0x00000000FFFFFFFFL;
			return longOp | longArg;
		}
		public static byte opFromLong(long lng) {
			return (byte)(lng >> 32);
		}
		public static int argFromLong(long lng) {
			return (int)(lng & 0x00000000FFFFFFFFL);
		}
		public static void main(String[] args) {
			 Encoding test = new Encoding(false, (byte)25, -15);
			 long val = test.asLong();
			 System.out.println(val);
			 System.out.println(Long.toBinaryString(val));
			 System.out.println(opFromLong(val));
			 System.out.println(argFromLong(val));
			 Encoding test1 = new Encoding(false, (byte)17, 15);
			 val = test1.asLong();
			 System.out.println(val);
			 System.out.println(Long.toBinaryString(val));
			 System.out.println(opFromLong(val));
			 System.out.println(argFromLong(val));
		}
}
