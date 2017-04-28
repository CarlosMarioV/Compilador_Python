package analizador;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import generated.MyParser;
import generated.MyParserBaseVisitor;
import jdk.nashorn.internal.parser.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

/**
 * Created by dell on 22/4/2017.
 */
public class analizadorContextual extends MyParserBaseVisitor<Object> {

    private int VACIO = 0, INT = 1, STRING = 2, CHAR = 3, LISTA = 4, ID = 5,
            MUL= 6, DIV= 7, SUMA = 8 , RESTA = 9;

    private TablaSimbolos tablaSimbolos;

    private Token primerSignoMD;
    private Token primerSignoSR;
    private ArrayList<TablaSimbolos> scopes;    //Lista de los Scope en el texto
    private String nombreVariable;              //Guarda el nombre de la variable temporalmente.
    private String variableAnalizar;            //Variable, controla si esta indefinida o no.
    private int profundidadScope;               //Profundidad en la que nos encontramos en el Scope.

    private ArrayList<Object> listaSimbolosAsignacion; //lista de los simbolos en las asignaciones.


    public analizadorContextual() {
        this.tablaSimbolos = new TablaSimbolos();
        this.scopes = new ArrayList<>();
        this.listaSimbolosAsignacion = new ArrayList<>();

        this.nombreVariable = null;
        this.variableAnalizar = null;
        this.profundidadScope = 0;

        this.scopes.add(profundidadScope, this.tablaSimbolos);
    }

    /**
     * Se sobrescriben todos los metodos de la clase MyParserBaseVisitor<?>, esto con el fin de adecuarlos para imprimir
     * en consola y en la GUI, se imprimiran los tokens cambiantes como los nombres de variables, expresiones primarias
     * (int | string) y los parametros de la función.
     */


    @Override
    public Object visitInicioPrograma(MyParser.InicioProgramaContext ctx) {

        visit(ctx.statement(0));
        for (int i=1; i <= ctx.statement().size()-1; i++)
        {
            visit(ctx.statement(i));
        }

        tablaSimbolos.imprimir();

        return null;
    }

    @Override
    public Object visitDeclaracionDEF(MyParser.DeclaracionDEFContext ctx) {

        visit(ctx.defStatement());

        return null;
    }

    @Override
    public Object visitDeclaracionIF(MyParser.DeclaracionIFContext ctx) {

        visit(ctx.ifStatement());

        return null;
    }

    @Override
    public Object visitDeclaracionRETURN(MyParser.DeclaracionRETURNContext ctx) {

        visit(ctx.returnStatement());

        return null;
    }

    @Override
    public Object visitDeclaracionPRINT(MyParser.DeclaracionPRINTContext ctx) {

        visit(ctx.printStatement());


        return null;
    }

    @Override
    public Object visitDeclaracionWHILE(MyParser.DeclaracionWHILEContext ctx) {

        visit(ctx.whileStatement());


        return null;
    }

    @Override
    public Object visitDeclaracionASSIGN(MyParser.DeclaracionASSIGNContext ctx) {

        visit(ctx.assignStatement());


        return null;
    }

    @Override
    public Object visitDeclaracionLLAMADAFUNCION(MyParser.DeclaracionLLAMADAFUNCIONContext ctx) {

        visit(ctx.functionCallStatement());


        return null;
    }

    @Override
    public Object visitFuncion(MyParser.FuncionContext ctx) {

        visit(ctx.argList());
        visit(ctx.sequence());

        return null;
    }

    @Override
    public Object visitListaParametros(MyParser.ListaParametrosContext ctx) {

        System.out.println(" Parametro --> " + ctx.IDENTIFIER().getSymbol().getText());


        visit(ctx.moreArgs());


        return null;
    }

    @Override
    public Object visitListaParametroEOS(MyParser.ListaParametroEOSContext ctx) {


        return null;
    }

    @Override
    public Object visitMasParametros(MyParser.MasParametrosContext ctx) {

        for (int i = 0; i <= ctx.IDENTIFIER().size() - 1; i++)
        {

            System.out.println(" Parametro --> " + ctx.IDENTIFIER(i).getSymbol().getText());


        }

        return null;
    }

    @Override
    public Object visitIf_y_Else(MyParser.If_y_ElseContext ctx)  {
        //Esperar a que termine

        visit(ctx.expression());

        visit(ctx.sequence(0));
        for (int i=1; i <= ctx.sequence().size()-1; i++)
        {
            visit(ctx.sequence(i));
        }

        return null;
    }

    @Override
    public Object visitWhile(MyParser.WhileContext ctx) {

        visit(ctx.expression());

        visit(ctx.sequence());


        return null;
    }

    @Override
    public Object visitReturn(MyParser.ReturnContext ctx) {

        visit(ctx.expression());

        return null;
    }

    @Override
    public Object visitPrint(MyParser.PrintContext ctx) {

        visit(ctx.expression());

        return null;
    }

    /**
     * Se encuentra el nombre de la variable a asignar y se guarda globalmente.
     * @param ctx
     * @return
     */
    @Override
    public Object visitAsignacion(MyParser.AsignacionContext ctx) {
        int nodoRaizAsignacion;

        nodoRaizAsignacion = (int) visit(ctx.expression()); //Realiza las visitas, tendrá que ir llenando una lista con los tokens.
        TablaSimbolos.Ident i = tablaSimbolos.buscar(ctx.IDENTIFIER().getText().toString());
        if (i != null) {
            int tipoTabla = i.tok.getType();

            if (tipoTabla != STRING) {
                System.out.println("Tipos incompatibles en fila: " + ctx.ASIGN().getSymbol().getLine() +
                        " columna: " + ctx.ASIGN().getSymbol().getCharPositionInLine());
            }
        } else {
            tablaSimbolos.insertar(ctx.IDENTIFIER().getText().toString(), nodoRaizAsignacion, ctx);
        }

        return null;
    }

    @Override
    public Object visitLlamarFuncion(MyParser.LlamarFuncionContext ctx) {

        visit(ctx.primitiveExpression());
        visit(ctx.expressionList());


        return null;
    }

    @Override
    public Object visitSecuencia(MyParser.SecuenciaContext ctx) {

        visit(ctx.moreStatement());

        return null;
    }

    @Override
    public Object visitMasDeclaraciones(MyParser.MasDeclaracionesContext ctx) {

        visit(ctx.statement(0));
        for (int i=1; i <= ctx.statement().size()-1; i++)
        {
            visit(ctx.statement(i));
        }

        return null;
    }

    @Override
    public Object visitExpresion(MyParser.ExpresionContext ctx) {
        int nodoIzquierdo, nodoDerecho;
        nodoIzquierdo = (int) visit(ctx.additionExpression());
        //todo: falta evaluar este nodo.
        visit(ctx.comparison());

        return nodoIzquierdo;
    }

    @Override
    public Object visitComparacion(MyParser.ComparacionContext ctx) {
        //Todo: falta evaluar esta parte del arbol.
        for (int i=0; i <= ctx.additionExpression().size()-1; i++)
        {
            visit(ctx.signosComparacion(i));
            visit(ctx.additionExpression(i));
        }

        return 0;
    }

    @Override
    public Object visitSignosComparacion(MyParser.SignosComparacionContext ctx) {

        return null;
    }

    @Override
    public Object visitEspresionAderencia(MyParser.EspresionAderenciaContext ctx) {
        int nodoIzquierdo, nodoDerecho;

        nodoIzquierdo = (int) visit(ctx.multiplicationExpression());
        nodoDerecho = (int) visit(ctx.additionFactor());

        if (nodoDerecho == VACIO){
            return nodoIzquierdo;
        }
        if (nodoIzquierdo != nodoDerecho){/*
        todo: capturar la excepción, de tipos incompatibles, no dejarlos hacer*/
            print("Tipos incompatible en fila: " + primerSignoSR.getLine() +
                    " columna: " + primerSignoSR.getCharPositionInLine());
        } else {
            return nodoIzquierdo;
        }
        primerSignoSR = null;
        //Si sale bien, retorna el tipo si son iguales.
        return nodoIzquierdo;
    }

    @Override
    public Object visitFactorAderencia(MyParser.FactorAderenciaContext ctx) {
        /*
        for (int i=0; i <= ctx.multiplicationExpression().size()-1; i++)
        {
            visit(ctx.signosSumaResta(i));
            print ("La visita otra " + visit(ctx.multiplicationExpression(i)) );
        }*/
        //En el caso de que solo haya un elemento de m = a + b
        if (ctx.multiplicationExpression().size() == 1) {
            primerSignoSR = (Token) visit(ctx.signosSumaResta(0));
            return visit(ctx.multiplicationExpression(0));
        }
        //En el caso de que sea asignación m = a
        if (ctx.multiplicationExpression().size() == 0) {
            return VACIO;
        }
        /*
        Compara cada visita para el caso de que haya mas variable m = a * b * c ...
        y se asegura que sea int cada una de lo contrario tira la excepción
         */
        int nodoAnterior, nodoSiguiente;

        nodoAnterior = (int) visit(ctx.multiplicationExpression(0));
        Token signo = (Token) visit(ctx.signosSumaResta(1));

        for (int i=1; i <= ctx.multiplicationExpression().size()-1; i++)
        {
            nodoSiguiente = (int) visit(ctx.multiplicationExpression(i));
            if (nodoAnterior != nodoSiguiente) {
                if (nodoAnterior == STRING && nodoSiguiente == STRING &&
                        signo.getType() != 22){
                    //todo: Capturar la excepción de que es "Carlos" *, / "Mario"
                    print("Operador incompatible en fila: " + signo.getLine() +
                            " columna: " + signo.getCharPositionInLine());
                } else if (nodoAnterior == INT && nodoSiguiente == STRING ||
                        nodoAnterior == STRING && nodoSiguiente == INT) {
                    print("Tipos incompatible en fila: " + signo.getLine() +
                            " columna: " + signo.getCharPositionInLine());
                }
            }
            if (i+1 <= ctx.signosSumaResta( ).size()-1) {
                signo = (Token) visit(ctx.signosSumaResta(i+1));
            }
            nodoAnterior = nodoSiguiente;
        }
        //Para el caso de que este bien.
        return nodoAnterior;
    }

    /**
     * Retorna los tokens de los signos de suma y resta.
     * @param ctx
     * @return
     */
    @Override
    public Object visitSignosSumaResta(MyParser.SignosSumaRestaContext ctx) {

        if (ctx.RESTA() != null) {
            return ctx.RESTA().getSymbol();
        } else {
            return ctx.SUMA().getSymbol();
        }
    }

    @Override
    public Object visitExpresionMultiplicacion(MyParser.ExpresionMultiplicacionContext ctx) {
        int nodoIzquierdo, nodoDerecho;

        nodoIzquierdo = (int) visit(ctx.elementExpression());
        nodoDerecho = (int) visit(ctx.multiplicationFactor());


        if (nodoDerecho == VACIO) {/*
        Si el nodo de la derecho viene vacío significa que hay una asignación.
        */
            return nodoIzquierdo;
        } else if (nodoIzquierdo == STRING || nodoDerecho == STRING) {/*
                Si los nodos fueran incompatibles con la multiplicación
                */
            //todo: Capturar la excepción, aquí se esta cayendo por el null con a = "Carlos" + "Mario" * 3
            print("Tipos incompatibles en fila: " + primerSignoMD.getLine() +
                    " columna: " + primerSignoMD.getCharPositionInLine());
            primerSignoMD = null;
            return null;
        } else {/*
        Para el caso de que salga bien*/
            return INT;
        }
    }

    @Override
    public Object visitFactorMultiplicacion(MyParser.FactorMultiplicacionContext ctx) {
        //En el caso de que solo haya un elemento de m = a * b
        if (ctx.elementExpression().size() == 1) {
            primerSignoMD = (Token) visit(ctx.signosOperativos(0));
            return visit(ctx.elementExpression(0));
        }
        //En el caso de que sea asignación m = a
        if (ctx.elementExpression().size() == 0) {
            return VACIO;
        }
        /*
        Compara cada visita para el caso de que haya mas variable m = a * b * c ...
        y se asegura que sea int cada una de lo contrario tira la excepción
         */
        int nodoAnterior, nodoSiguiente;

        nodoAnterior = (int) visit(ctx.elementExpression(0));
        Token signo = (Token) visit(ctx.signosOperativos(1));

        for (int i=1; i <= ctx.elementExpression().size()-1; i++)
        {
            nodoSiguiente = (int) visit(ctx.elementExpression(i));
            if (nodoAnterior != INT || nodoSiguiente != INT) {

                //capturar la excepción Aquí.

                print("Tipos incompatibles en fila: " + signo.getLine() +
                " columna: " + signo.getCharPositionInLine());
            }
            if (i+1 <= ctx.signosOperativos( ).size()-1) {
                signo = (Token) visit(ctx.signosOperativos(i+1));
            }
            nodoAnterior = nodoSiguiente;
        }
        //Para el caso de que este bien.
        return INT;
    }

    /**
     * Retorna los tokens de los símbolos de multiplicación o división
     * @param ctx
     * @return
     */
    @Override
    public Object visitSignosOperativos(MyParser.SignosOperativosContext ctx) {

        if (ctx.MUL() != null) {
            return ctx.MUL().getSymbol();
        } else {
            return ctx.DIV().getSymbol();
        }
    }

    @Override
    public Object visitExpresionElemento(MyParser.ExpresionElementoContext ctx) {
        int nodoIzquierdo, nodoDerecho;

         nodoIzquierdo = (int) visit(ctx.primitiveExpression());
         nodoDerecho = (int) visit(ctx.elementAccess());
        //this.listaSimbolosAsignacion.add(variable);

        if (nodoDerecho == VACIO){
            return nodoIzquierdo;
        } else if (nodoIzquierdo == nodoDerecho) {
            return nodoDerecho;
        }
        return null;
    }

    @Override
    public Object visitAccesoElemento(MyParser.AccesoElementoContext ctx) {
        //No se que expresion evalua esto
        for (int i=0; i <= ctx.expression().size()-1; i++)
        {
            visit(ctx.expression(i));
        }

        return 0;
    }

    @Override
    public Object visitDeclaracionFuntionCallExpression(MyParser.DeclaracionFuntionCallExpressionContext ctx) {

        visit(ctx.expressionList());

        return null;
    }

    @Override
    public Object visitListaExpresiones(MyParser.ListaExpresionesContext ctx) {

        visit(ctx.expression());
        visit(ctx.moreExpressions());

        return null;
    }

    @Override
    public Object visitListExpressionEOS(MyParser.ListExpressionEOSContext ctx) {
        //Espacio en blanco

        return null;
    }

    @Override
    public Object visitMasExpresiones(MyParser.MasExpresionesContext ctx) {

        for (int i=0; i <= ctx.expression().size()-1; i++)
        {
            visit(ctx.expression(i));
        }

        return null;
    }

    @Override
    public Object visitExpresionPrimitivaINT(MyParser.ExpresionPrimitivaINTContext ctx) {
        /*
        if (nombreVariable != null) {
            TablaSimbolos.Ident i = tablaSimbolos.buscar(nombreVariable);
            if (i != null) {
                int tipoTabla;
                tipoTabla = i.tok.getType();

                if (tipoTabla != INT) {
                    System.out.println("Tipos incompatibles en fila: " + ctx.INTEGER().getSymbol().getLine() +
                            " columna: " + ctx.INTEGER().getSymbol().getCharPositionInLine());
                }
            } else {
                tablaSimbolos.insertar(nombreVariable, INT, ctx);
            }
        }
        nombreVariable = null;
        */
        return INT;
    }

    @Override
    public Object visitExpresionPrimitivaSTRING(MyParser.ExpresionPrimitivaSTRINGContext ctx) {
        /*
        if (nombreVariable != null) {
            TablaSimbolos.Ident i = tablaSimbolos.buscar(nombreVariable);
            if (i != null) {
                int tipoTabla = i.tok.getType();

                if (tipoTabla != STRING) {
                    System.out.println("Tipos incompatibles en fila: " + ctx.STRING().getSymbol().getLine() +
                            " columna: " + ctx.STRING().getSymbol().getCharPositionInLine());
                }
            } else {
                tablaSimbolos.insertar(nombreVariable, STRING, ctx);
            }
        }
        nombreVariable = null;
        */
        return STRING;
    }

    /**
     * Cuando se ejecuta la expresion m = a +* b devuelve a, b
     * @param ctx
     * @return
     */
    @Override
    public Object visitExpresionPrimitivaID(MyParser.ExpresionPrimitivaIDContext ctx) {
        //Retorno la variable para buscarla.
        variableAnalizar = ctx.IDENTIFIER().getText();
        System.out.println(ctx.getClass().getSimpleName() + " Token: " + variableAnalizar);

        return variableAnalizar;
    }

    @Override
    public Object visitExpresionPrimitivaCHAR(MyParser.ExpresionPrimitivaCHARContext ctx) {
        /*
        if (nombreVariable != null) {
            TablaSimbolos.Ident i = tablaSimbolos.buscar(nombreVariable);
            if (i != null) {
                int tipoTabla = i.tok.getType();

                if (tipoTabla != CHAR) {
                    System.out.println("Tipos incompatibles en fila: " + ctx.CHAR().getSymbol().getLine() +
                            " columna: " + ctx.CHAR().getSymbol().getCharPositionInLine());
                }
            } else {
                tablaSimbolos.insertar(nombreVariable, CHAR, ctx);
            }
        }
        nombreVariable = null;
        */
        return CHAR;
    }

    @Override
    public Object visitExpresionPrimitivaDER_EX_IZQ(MyParser.ExpresionPrimitivaDER_EX_IZQContext ctx) {

        visit(ctx.expression());

        return null;
    }

    @Override
    public Object visitExpresionPrimitivaLISTAEXPRESION(MyParser.ExpresionPrimitivaLISTAEXPRESIONContext ctx) {

        visit(ctx.listExpression());

        return LISTA;
    }

    @Override
    public Object visitExpresionPrimitivaLEN_PIZQ_EX_PDER(MyParser.ExpresionPrimitivaLEN_PIZQ_EX_PDERContext ctx) {

        visit(ctx.expression());

        return null;
    }

    @Override
    public Object visitExpresionPrimitivaFunctionCallExpression(MyParser.ExpresionPrimitivaFunctionCallExpressionContext ctx) {

        visit(ctx.functionCallExpression());

        return null;
    }

    @Override
    public Object visitListaExpresionesUltima(MyParser.ListaExpresionesUltimaContext ctx) {

        /* print("Estoy en lista");
        if (nombreVariable != null) {
            TablaSimbolos.Ident i = tablaSimbolos.buscar(nombreVariable);
            if (i != null) {
                int tipoTabla = i.tok.getType();

                if (tipoTabla != LISTA) {
                    System.out.println("Tipos incompatibles en fila: " + ctx.PCIZQ().getSymbol().getLine() +
                            " columna: " + ctx.PCIZQ().getSymbol().getCharPositionInLine());
                }
            } else {
                tablaSimbolos.insertar(nombreVariable, LISTA, ctx);
            }
        }
        nombreVariable = null;
        */
        visit(ctx.expressionList());

        return null;
    }

    public void print( Object doc) {
        System.out.println(doc);
    }

    public boolean isString(Object str) {
        if (str.equals(str.toString())) {
            return true;
        } else {
            return false;
        }
    }
}
