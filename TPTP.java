/* TPTP.java */
/* Generated By:JavaCC: Do not edit this line. TPTP.java */
import java.util.*;

public class TPTP implements TPTPConstants {
  // Use a LinkedHashMap to preserve source order.
  static Map<String, Step> steps = new LinkedHashMap<>();
  static Set<String> stepNames = new HashSet<>();
  static String startStep;
  static int startX, startY;
  // Field to store the condition variable name of the last parsed condition.
  static String currentConditionVar;

  // (s₁) Check if an arithmetic expression is positive linear.
  // If disallowSubtraction is true then no '-' is allowed.
  public static boolean isPositiveLinear(String expr, boolean disallowSubtraction) {
      if(expr.indexOf('*') != -1) return false;
      if(disallowSubtraction && expr.indexOf('-') != -1) return false;
      for (int i = 0; i < expr.length(); i++) {
          if(expr.charAt(i) == '-') {
              if(i+1 >= expr.length() || !Character.isDigit(expr.charAt(i+1))) {
                  return false;
              }
          }
      }
      return true;
  }

  // (s₃) Check that when the condition is false the else expressions evaluate nonnegative.
  // The substitution depends on which parameter is used in the condition.
  public static boolean elseExpressionsNonNegative(String expr1, String expr2, String param1, String param2, int condVal, String condVar) {
      int val1, val2;
      if(condVar.equals(param1)) {
         // Condition variable is the first parameter: test with (condVal, 0)
         val1 = evaluate(expr1, condVal, 0, param1, param2);
         val2 = evaluate(expr2, condVal, 0, param1, param2);
      } else if(condVar.equals(param2)) {
         // Otherwise, test with (0, condVal)
         val1 = evaluate(expr1, 0, condVal, param1, param2);
         val2 = evaluate(expr2, 0, condVal, param1, param2);
      } else {
         // Fallback: use (condVal, 0)
         val1 = evaluate(expr1, condVal, 0, param1, param2);
         val2 = evaluate(expr2, condVal, 0, param1, param2);
      }
      return (val1 >= 0 && val2 >= 0);
  }

  // Evaluates an arithmetic expression using the current parameter values.
  public static int evaluate(String expr, int x, int y, String param1, String param2) {
      expr = expr.trim();
      if(expr.startsWith(param1)) {
         if(expr.equals(param1)) return x;
         int len = param1.length();
         char op = expr.charAt(len);
         int num = Integer.parseInt(expr.substring(len+1));
         if(op == '-') return x - num;
         else if(op == '+') return x + num;
      } else if(expr.startsWith(param2)) {
         if(expr.equals(param2)) return y;
         int len = param2.length();
         char op = expr.charAt(len);
         int num = Integer.parseInt(expr.substring(len+1));
         if(op == '-') return y - num;
         else if(op == '+') return y + num;
      }
      try {
          return Integer.parseInt(expr);
      } catch(Exception e) {
          return 0;
      }
  }

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

          String state = currentStep + "," + x + "," + y;
          if (visited.contains(state)) {
              System.out.println("Loop");
              return;
          }
          visited.add(state);

          Step step = steps.get(currentStep);
          int oldX = x, oldY = y;
          int newX, newY;
          if (oldX < step.conditionValue) {
              newX = evaluate(step.becomesExpr1, oldX, oldY, step.param1Name, step.param2Name);
              newY = evaluate(step.becomesExpr2, oldX, oldY, step.param1Name, step.param2Name);
              currentStep = step.nextStep;
          } else {
              newX = evaluate(step.elseExpr1, oldX, oldY, step.param1Name, step.param2Name);
              newY = evaluate(step.elseExpr2, oldX, oldY, step.param1Name, step.param2Name);
              currentStep = step.elseStep;
          }
          x = newX;
          y = newY;
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
    jj_consume_token(0);
}

  final public void StepInstruction() throws ParseException {Token stepName, param1, param2, temp;
  String nextStep = null, elseStep = null;
  String name;
  int conditionValue = 0;
  boolean hasBecomes = false, hasElseBlock = false;
  // Variables for arithmetic expressions.
  String becomesExpr1 = null, becomesExpr2 = null, elseExpr1 = null, elseExpr2 = null;
  // Temporary strings.
  String tempStr1 = null, tempStr2 = null;
  // Parameter names.
  String p1Name = null, p2Name = null;
    // Parse step name.
      stepName = jj_consume_token(ID);
name = stepName.image;
      if (TPTP.stepNames.contains(name)) {
          {if (true) throw new ParseException("Duplicate step name: " + name);}
      }
      TPTP.stepNames.add(name);
    jj_consume_token(COLON);
    jj_consume_token(IF);
    // Parse condition and parameter list (ID, ID). Condition() returns an int
      // and sets TPTP.currentConditionVar.
      conditionValue = Condition();
    jj_consume_token(LPAREN);
    param1 = jj_consume_token(ID);
    jj_consume_token(COMMA);
    param2 = jj_consume_token(ID);
    jj_consume_token(RPAREN);
p1Name = param1.image;
      p2Name = param2.image;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case BECOMES:{
      jj_consume_token(BECOMES);
      jj_consume_token(LPAREN);
tempStr1 = ArithmeticExpressionString();
      jj_consume_token(COMMA);
tempStr2 = ArithmeticExpressionString();
      jj_consume_token(RPAREN);
      jj_consume_token(AND);
      temp = jj_consume_token(ID);
nextStep = temp.image; hasBecomes = true; becomesExpr1 = tempStr1; becomesExpr2 = tempStr2;
      break;
      }
    case ID:{
      // Option 2: Short form.
          temp = jj_consume_token(ID);
nextStep = temp.image;
      break;
      }
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(ELSE);
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case LPAREN:{
      jj_consume_token(LPAREN);
tempStr1 = ArithmeticExpressionString();
      jj_consume_token(COMMA);
tempStr2 = ArithmeticExpressionString();
      jj_consume_token(RPAREN);
      jj_consume_token(AND);
      temp = jj_consume_token(ID);
elseStep = temp.image; hasElseBlock = true; elseExpr1 = tempStr1; elseExpr2 = tempStr2;
      break;
      }
    case ID:{
      // Option 2: Short form.
          temp = jj_consume_token(ID);
elseStep = temp.image;
      break;
      }
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
// If arithmetic expressions weren't provided, default to identity.
      if (!hasBecomes) { becomesExpr1 = p1Name; becomesExpr2 = p2Name; }
      if (!hasElseBlock) { elseExpr1 = p1Name; elseExpr2 = p2Name; }

      // Determine simplicity:
      boolean simple = true;
      // (s₁): All expressions must be positive linear.
      if (!TPTP.isPositiveLinear(becomesExpr1, true)) simple = false;
      if (!TPTP.isPositiveLinear(becomesExpr2, true)) simple = false;
      if (!TPTP.isPositiveLinear(elseExpr1, false)) simple = false;
      if (!TPTP.isPositiveLinear(elseExpr2, false)) simple = false;
      // (s₂): In the becomes branch, no subtraction is allowed.
      if (hasBecomes) {
          if (becomesExpr1.indexOf('-') != -1 || becomesExpr2.indexOf('-') != -1) simple = false;
      }
      // (s₃): For nonnegative parameters making the condition false,
      // the else expressions must evaluate to nonnegative.
      if (!TPTP.elseExpressionsNonNegative(elseExpr1, elseExpr2, p1Name, p2Name, conditionValue, TPTP.currentConditionVar)) simple = false;

      TPTP.steps.put(name, new Step(name, conditionValue, nextStep, elseStep, simple,
                                     becomesExpr1, becomesExpr2, elseExpr1, elseExpr2,
                                     p1Name, p2Name));
}

  final public int Condition() throws ParseException {Token id, num;
    id = jj_consume_token(ID);
    jj_consume_token(LESS_THAN);
    num = jj_consume_token(NUM);
TPTP.currentConditionVar = id.image; {if ("" != null) return Integer.parseInt(num.image);}
    throw new Error("Missing return statement in function");
}

  final public String ArithmeticExpressionString() throws ParseException {Token t;
  StringBuffer sb = new StringBuffer();
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case NUM:{
      t = jj_consume_token(NUM);
sb.append(t.image);
      break;
      }
    case ID:{
      t = jj_consume_token(ID);
sb.append(t.image);
      break;
      }
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case 18:
      case 19:
      case 20:{
        ;
        break;
        }
      default:
        jj_la1[4] = jj_gen;
        break label_2;
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case 18:{
        jj_consume_token(18);
sb.append("-");
        break;
        }
      case 19:{
        jj_consume_token(19);
sb.append("+");
        break;
        }
      case 20:{
        jj_consume_token(20);
sb.append("*");
        break;
        }
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case NUM:{
        t = jj_consume_token(NUM);
sb.append(t.image);
        break;
        }
      case ID:{
        t = jj_consume_token(ID);
sb.append(t.image);
        break;
        }
      default:
        jj_la1[6] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
{if ("" != null) return sb.toString();}
    throw new Error("Missing return statement in function");
}

  final public void RunInstruction() throws ParseException {Token step, n1, n2;
    jj_consume_token(RUN);
    step = jj_consume_token(ID);
    jj_consume_token(LPAREN);
    n1 = jj_consume_token(NUM);
    jj_consume_token(COMMA);
    n2 = jj_consume_token(NUM);
    jj_consume_token(RPAREN);
TPTP.startStep = step.image;
      TPTP.startX = Integer.parseInt(n1.image);
      TPTP.startY = Integer.parseInt(n2.image);
}

  final public void Expression() throws ParseException {
    ArithmeticExpressionString();
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
  final private int[] jj_la1 = new int[7];
  static private int[] jj_la1_0;
  static {
	   jj_la1_init_0();
	}
	private static void jj_la1_init_0() {
	   jj_la1_0 = new int[] {0x2000,0x2004,0x2100,0x3000,0x1c0000,0x1c0000,0x3000,};
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
	 for (int i = 0; i < 7; i++) jj_la1[i] = -1;
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
	 for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public TPTP(java.io.Reader stream) {
	 jj_input_stream = new SimpleCharStream(stream, 1, 1);
	 token_source = new TPTPTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 7; i++) jj_la1[i] = -1;
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
	 for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public TPTP(TPTPTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(TPTPTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 7; i++) jj_la1[i] = -1;
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
	 for (int i = 0; i < 7; i++) {
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
  String nextStep, elseStep;
  boolean isSimple;
  // Arithmetic expression strings.
  String becomesExpr1, becomesExpr2, elseExpr1, elseExpr2;
  // Parameter names for this step.
  String param1Name, param2Name;

  Step(String name, int conditionValue, String nextStep, String elseStep, boolean isSimple,
       String becomesExpr1, String becomesExpr2, String elseExpr1, String elseExpr2,
       String param1Name, String param2Name) {
      this.name = name;
      this.conditionValue = conditionValue;
      this.nextStep = nextStep;
      this.elseStep = elseStep;
      this.isSimple = isSimple;
      this.becomesExpr1 = becomesExpr1;
      this.becomesExpr2 = becomesExpr2;
      this.elseExpr1 = elseExpr1;
      this.elseExpr2 = elseExpr2;
      this.param1Name = param1Name;
      this.param2Name = param2Name;
  }
}
