/* TPTP.java */
/* Generated By:JavaCC: Do not edit this line. TPTP.java */
import java.util.*;

public class TPTP implements TPTPConstants {
  static Map<String, Step> steps = new HashMap<>();
  static Set<String> stepNames = new HashSet<>();
  static String startStep;
  static int startX, startY;

  public static void main(String[] args) throws ParseException {
      TPTP parser = new TPTP(System.in);
      try {
          parser.Program();
          System.out.println("Pass");
          if (isSimple()) {
              System.out.println("Simple");
              execute();
          } else {
              System.out.println("Non-simple");
              System.out.println(getFirstNonSimpleStep());
          }
      } catch (ParseException e) {
          System.out.println("Fail");
          System.err.println(e.getMessage());
      }
  }

  static boolean isSimple() {
      for (Step step : steps.values()) {
          if (!step.isSimple) return false;
      }
      return true;
  }

  static String getFirstNonSimpleStep() {
      for (Step step : steps.values()) {
          if (!step.isSimple) return step.name;
      }
      return "";
  }

  static void execute() {
      Set<String> visited = new HashSet<>();
      int x = startX, y = startY;
      String currentStep = startStep;

      while (steps.containsKey(currentStep)) {
          if (x < 0 || y < 0 || x > 1000000000 || y > 1000000000) {
              System.out.println("Fail");
              System.err.println("Robot coordinates out of bounds.");
              return;
          }

          if (visited.contains(currentStep + "," + x + "," + y)) {
              System.out.println("Loop");
              return;
          }
          visited.add(currentStep + "," + x + "," + y);
          Step step = steps.get(currentStep);
          if (x < step.conditionValue) {
              x = step.e1;
              y = step.e2;
              currentStep = step.nextStep;
          } else {
              x = step.e3;
              y = step.e4;
              currentStep = step.elseStep;
          }
      }
      System.out.println(currentStep + " " + x + " " + y);
  }

  final public void Program() throws ParseException {
    label_1:
    while (true) {
      StepInstruction();
      jj_consume_token(SEMICOLON);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case ID:{
        ;
        break;
        }
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
    }
    RunInstruction();
}

  final public void StepInstruction() throws ParseException {Token t;
  String name, nextStep, elseStep;
  int conditionValue, e1, e2, e3, e4;
  boolean isSimple = true;
    t = jj_consume_token(ID);
name = t.image; // Convert token to string
      if (stepNames.contains(name)) {
          {if (true) throw new ParseException("Duplicate step name: " + name);}
      }
      stepNames.add(name);
    jj_consume_token(COLON);
    jj_consume_token(IF);
    Condition();
    jj_consume_token(LPAREN);
    jj_consume_token(ID);
    jj_consume_token(COMMA);
    jj_consume_token(ID);
    jj_consume_token(RPAREN);
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case ID:{
      jj_consume_token(ID);
      break;
      }
    default:
      jj_la1[1] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case BECOMES:{
      jj_consume_token(BECOMES);
      jj_consume_token(LPAREN);
      Expression();
      jj_consume_token(COMMA);
      Expression();
      jj_consume_token(RPAREN);
      jj_consume_token(AND);
      jj_consume_token(ID);
      break;
      }
    default:
      jj_la1[2] = jj_gen;
      ;
    }
    jj_consume_token(ELSE);
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case ID:{
      jj_consume_token(ID);
      jj_consume_token(SEMICOLON);
      break;
      }
    default:
      jj_la1[3] = jj_gen;
      ;
    }
    jj_consume_token(LPAREN);
    Expression();
    jj_consume_token(COMMA);
    Expression();
    jj_consume_token(RPAREN);
    jj_consume_token(AND);
    jj_consume_token(ID);
}

  final public void Condition() throws ParseException {
    jj_consume_token(ID);
    jj_consume_token(LT);
    jj_consume_token(NUM);
}

  final public void Expression() throws ParseException {
    Term();
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case PLUS:
      case MINUS:{
        ;
        break;
        }
      default:
        jj_la1[4] = jj_gen;
        break label_2;
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case MINUS:{
        jj_consume_token(MINUS);
        break;
        }
      case PLUS:{
        jj_consume_token(PLUS);
        break;
        }
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      Term();
    }
}

  final public void Term() throws ParseException {
    Factor();
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case MULT:{
        ;
        break;
        }
      default:
        jj_la1[6] = jj_gen;
        break label_3;
      }
      jj_consume_token(MULT);
      Factor();
    }
}

  final public void Factor() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case NUM:{
      jj_consume_token(NUM);
      break;
      }
    case ID:{
      jj_consume_token(ID);
      break;
      }
    case LPAREN:{
      jj_consume_token(LPAREN);
      Expression();
      jj_consume_token(RPAREN);
      break;
      }
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
}

  final public void RunInstruction() throws ParseException {
    jj_consume_token(RUN);
    jj_consume_token(ID);
    jj_consume_token(LPAREN);
    jj_consume_token(NUM);
    jj_consume_token(COMMA);
    jj_consume_token(NUM);
    jj_consume_token(RPAREN);
}

  /** Generated Token Manager. */
  public TPTPTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[8];
  static private int[] jj_la1_0;
  static {
	   jj_la1_init_0();
	}
	private static void jj_la1_init_0() {
	   jj_la1_0 = new int[] {0x10000,0x10000,0x4,0x10000,0x1800,0x1800,0x2000,0x18100,};
	}

  /** Constructor with InputStream. */
  public TPTP(java.io.InputStream stream) {
	  this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public TPTP(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source = new TPTPTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
	  ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public TPTP(java.io.Reader stream) {
	 jj_input_stream = new SimpleCharStream(stream, 1, 1);
	 token_source = new TPTPTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
	if (jj_input_stream == null) {
	   jj_input_stream = new SimpleCharStream(stream, 1, 1);
	} else {
	   jj_input_stream.ReInit(stream, 1, 1);
	}
	if (token_source == null) {
 token_source = new TPTPTokenManager(jj_input_stream);
	}

	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public TPTP(TPTPTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(TPTPTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
	 Token oldToken;
	 if ((oldToken = token).next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 if (token.kind == kind) {
	   jj_gen++;
	   return token;
	 }
	 token = oldToken;
	 jj_kind = kind;
	 throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
	 if (token.next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 jj_gen++;
	 return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
	 Token t = token;
	 for (int i = 0; i < index; i++) {
	   if (t.next != null) t = t.next;
	   else t = t.next = token_source.getNextToken();
	 }
	 return t;
  }

  private int jj_ntk_f() {
	 if ((jj_nt=token.next) == null)
	   return (jj_ntk = (token.next=token_source.getNextToken()).kind);
	 else
	   return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
	 jj_expentries.clear();
	 boolean[] la1tokens = new boolean[21];
	 if (jj_kind >= 0) {
	   la1tokens[jj_kind] = true;
	   jj_kind = -1;
	 }
	 for (int i = 0; i < 8; i++) {
	   if (jj_la1[i] == jj_gen) {
		 for (int j = 0; j < 32; j++) {
		   if ((jj_la1_0[i] & (1<<j)) != 0) {
			 la1tokens[j] = true;
		   }
		 }
	   }
	 }
	 for (int i = 0; i < 21; i++) {
	   if (la1tokens[i]) {
		 jj_expentry = new int[1];
		 jj_expentry[0] = i;
		 jj_expentries.add(jj_expentry);
	   }
	 }
	 int[][] exptokseq = new int[jj_expentries.size()][];
	 for (int i = 0; i < jj_expentries.size(); i++) {
	   exptokseq[i] = jj_expentries.get(i);
	 }
	 return new ParseException(token, exptokseq, tokenImage);
  }

  private boolean trace_enabled;

/** Trace enabled. */
  final public boolean trace_enabled() {
	 return trace_enabled;
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}

class Step {
  String name;
  int conditionValue;
  int e1, e2, e3, e4;
  String nextStep, elseStep;
  boolean isSimple;

  Step(String name, int conditionValue, int e1, int e2, String nextStep, int e3, int e4, String elseStep, boolean isSimple) {
      this.name = name;
      this.conditionValue = conditionValue;
      this.e1 = e1;
      this.e2 = e2;
      this.nextStep = nextStep;
      this.e3 = e3;
      this.e4 = e4;
      this.elseStep = elseStep;
      this.isSimple = isSimple;
  }
}
