abstract class Corps {
    abstract public String transform();
}

abstract class Declarations {
    abstract public String transform();
}

class ConstructDeclarations extends Declarations{
    ElementsDeclaration e;
    Declarations d;
    public ConstructDeclarations (Declarations d,ElementsDeclaration e){
        this.d =d;
        this.e = e;
    }
    @Override
    public String transform() { return e.transform(); }
}

abstract class ElementsDeclaration extends Declarations{
    abstract public String transform();
}

class Abb extends ElementsDeclaration {
    Sym s;
    SuiteElements se;

    public Abb(Sym s, SuiteElements se){
        this.s = s;
        this.se = se;
    }

    @Override
    public String transform() {
        return "";
    }
}

class ConstructAbb extends Element{
    Declarations abreviation;
    public ConstructAbb(Declarations abreviation){
        this.abreviation = abreviation;
    }

    @Override
    public String transform() {
        if (((Abb)abreviation).s == Sym.BFBEG){
            return "<b>" + ((Abb)abreviation).se.transform() + "</b>";
        }else return "<i>" + ((Abb)abreviation).se.transform() + "</i>";
    }
}

class ValCol extends ElementsDeclaration {
    private String id;
    private String valeur;
    public ValCol (String id, String valeur){
        this.id = id;
        this.valeur = valeur;
    }
    @Override
    public String transform() { return "<font color = #" + valeur + " >"+ "</font>"; }
}

class ConstructCol extends Element{
    private String valeur;
    SuiteElements se;

    public ConstructCol(String valeur, SuiteElements se){
        this.valeur = valeur;
        this.se = se;
    }

    @Override
    public String transform() {
        return "<font color = #" + valeur + " > " +se.transform() + " </font>";
    }
}

class ConstructCorps extends Corps{
    SuiteElements se;
    public ConstructCorps(SuiteElements se){
        this.se = se;
    }
    @Override
    public String transform() {
        if(se == null){
            return "<!DOCTYPE html>\n<html>\n<body>\n" + "\n</body>\n</html>";
        }
        return "<!DOCTYPE html>\n<html>\n<body>\n" + se.transform() + "\n</body>\n</html>";
    }
}

abstract class Element extends Corps{
    abstract public String transform();
}

class Mot extends Element{
    private String value;
    public Mot (String value){
        this.value = value;
    }
    @Override
    public String transform(){
        return this.value;
    }
}

class Linebreak extends Element{
    private String value;
    public Linebreak(String value){
        this.value = value;
    }

    @Override
    public String transform() { return " <br>"; }
}

class It extends Element{
    SuiteElements s;
    public It(SuiteElements s){
        this.s = s;
    }
    public String transform() {
        if(this.s == null){
            return "</i>";
        }else {
            return "<i>" + s.transform() + "</i>";
        }
    }
}

class Bf extends Element{
    SuiteElements s;
    public Bf(SuiteElements s){
        this.s = s;
    }
    @Override
    public String transform() {
        if(this.s == null) {
            return "</b>";
        }else{
            return "<b>" + s.transform() + "</b>";
        }
    }
}

abstract class Enumeration extends Element{
    public abstract String transform();
}

class ConstructEnumeration extends Enumeration{
    SuiteItems se;
    public ConstructEnumeration(SuiteItems se){
        this.se = se;
    }
    public String transform() {
        if(this.se == null){
            return "\n" + "<ol>" + "</ol>";
        } else{
            return "\n" + "<ol>" + se.transform() + "</ol>";
        }
    }
}

abstract class SuiteElements extends Corps{
    abstract public String transform();
    abstract public boolean isEmpty();
}

class ConstructSuiteElem extends SuiteElements{
    Element e;
    Corps se;

    public ConstructSuiteElem(Element e1, Corps se1){
        this.e = e1;
        this.se = se1;
    }
    @Override
    public String transform() {
        if(this.e == null && this.se == null){
            return "";
        }else if(this.se == null && this.e != null){
            return e.transform() + "";
        }else if(this.e == null && this.se != null){
            return "" + se.transform();
        }else {
            return this.e.transform() + this.se.transform();
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}


class SuiteItems extends Enumeration{
    Item se;
    SuiteItems si;

    public SuiteItems(){
    }

    public SuiteItems(Item se1, SuiteItems si1){
        this.se = se1;
        this.si = si1;
    }

    @Override
    public String transform() {
        if(this.si == null && this.se == null){
            return "";
        }else if(this.se == null && this.si != null){
            return si.transform() + "";
        }else if(this.si == null && this.se != null){
            return "" + se.transform();
        }else {
            return this.se.transform() + this.si.transform();
        }
    }
}


class Item extends SuiteItems{
    private SuiteElements se;
    public Item(SuiteElements se){
        this.se = se;
    }
    public String transform(){
        if(this.se == null){
            return "";
        }else {
            return "\n <li>" + se.transform() + "</li>";
        }
    }
}


/*Suivre la grammaire pour construire l'arbre de syntaxe abstraite*/

