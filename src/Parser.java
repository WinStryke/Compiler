import java.io.*;
import java.util.HashMap;

class Parser{
    /*

    Grammaire:

    DOCUMENTS         ->     DECLARATIONS CORPS
    DECLARATIONS      ->     \set{ID} {VAL_COL} DECLARATIONS | e
    CORPS             ->     \begindoc SUITE_ELEMENTS \enddoc
    SUITE_ELEMENTS    ->     ELEMENTS SUITE_ELEMENTS | e
    ELEMENT           ->     MOT | LINEBREAK | \bf{SUITE_ELEMENTS} | \it{SUITE_ELEMENTS} | ENUMERATION | \couleur{VAL_COL}{SUITE_ELEMENTS}
    ENUMERATION       ->     \beginenum SUITE_ITEMS \endenum
    SUITE_ITEMS       ->     ITEM SUITE_ITEMS | e
    ITEM              ->     \item SUITE-ELEMENTS
    VAL_COL           ->     \constante_couleur | ID

    */
    public static HashMap<String, String> color = new HashMap<>();

    protected LookAhead1 reader;
    public Parser(LookAhead1 r) {
        reader=r;
    }

    public Document docu() throws Exception{
        return new ConstructDocument(beginDeclaration(),document());
    }

    public Declarations beginDeclaration() throws Exception {
        if (reader.check(Sym.SETCOL)) {
            return declare();
        }
        return null;
    }

    public Declarations declare() throws Exception{
        if(reader.check(Sym.SETCOL)){
            reader.eat(Sym.SETCOL);
            Declarations d = constCol();
            //reader.eat(Sym.AD);
            return d;
        }
        return null;
    }

    public Declarations constCol() throws Exception{
        String id = "";
        String valeur = "";
        if(reader.check(Sym.SETCOL)){ reader.eat(Sym.SETCOL);}
        if (reader.check(Sym.AG)) {
            reader.eat(Sym.AG);
            if (reader.check(Sym.MOT)) {
                id = reader.getValue();
                reader.eat(Sym.MOT);
            }
            reader.eat(Sym.AD);
            if (reader.check(Sym.AG)) {
                reader.eat(Sym.AG);
                if (reader.check(Sym.MOT)) {
                    valeur = reader.getValue();
                    reader.eat(Sym.MOT);
                }
                reader.eat(Sym.AD);
                color.put(id,valeur);
                return new ConstructDeclarations(new Cons_Col(id,valeur), constCol());
            }
        }
        return null;
    }

    public Corps document() throws Exception {
        reader.eat(Sym.BEGINDOC);
        Corps corps = new ConstructCorps(suiteelem());
        reader.eat(Sym.ENDDOC);
        reader.eat(Sym.EOF);
        return corps;
    }

    public SuiteElements suiteelem() throws Exception{
        if(reader.check(Sym.MOT)){
            String s = reader.getValue();
            reader.eat(Sym.MOT);
            return new ConstructSuiteElem(new Mot(s),suiteelem());
        }else if(reader.check(Sym.LINEBREAK)){
            reader.eat(Sym.LINEBREAK);
            Linebreak l = new Linebreak("\n");
            return new ConstructSuiteElem(l,suiteelem());
        }else if(reader.check(Sym.BFBEG)){
            return new ConstructSuiteElem(bfbeg(),suiteelem());
        }else if(reader.check(Sym.AD)){
            reader.eat(Sym.AD);
        }else if (reader.check(Sym.ITBEG)){
            return new ConstructSuiteElem(itbeg(),suiteelem());
        }else if(reader.check(Sym.BEGINENUM)){
            reader.eat(Sym.BEGINENUM);
            return new ConstructSuiteElem(enumerate(),suiteelem());
        }else if(reader.check(Sym.COULEUR)){
            return new ConstructSuiteElem(couleur(), suiteelem());
        }else if(reader.check(Sym.ABB)){
            reader.eat(Sym.ABB);
            return new ConstructSuiteElem(abbr(),suiteelem());
        }
        return null;
    }

    public Element abbr() throws Exception{
        String ab = "";
        String valeur = "";
        if (reader.check(Sym.AG)) {
            reader.eat(Sym.AG);
            ab = reader.getValue();
            reader.eat(Sym.MOT);
            reader.eat(Sym.AD);
        }
        if(reader.check(Sym.AG)){
            reader.eat(Sym.AG);
            valeur = reader.getValue();
            reader.eat(Sym.MOT);
            reader.eat(Sym.AD);
            return new Abb(valeur,ab);
        }
      return null;
    }

    public Element couleur() throws Exception {
        String valeur ="";
        reader.eat(Sym.COULEUR);
        reader.eat(Sym.AG);
        if(reader.check(Sym.MOT)){
            valeur = reader.getValue();
            for(String s : color.keySet()){
                if(s.equals(valeur)){
                    valeur = color.get(s);
                }
            }
            reader.eat(Sym.MOT);
        }
        reader.eat(Sym.AD);
        reader.eat(Sym.AG);
        SuiteElements se = suiteelem();
        return new ConstructCol(valeur, se);
    }

    public Element bfbeg() throws Exception{
        reader.eat(Sym.BFBEG);
        if(reader.check(Sym.AG)) {
            reader.eat(Sym.AG);
            return new Bf(suiteelem());
        }else throw new Exception("AG of Bf cannot be reduce");
    }

    public Element itbeg() throws Exception {
        reader.eat(Sym.ITBEG);
        if (reader.check(Sym.AG)) {
            reader.eat(Sym.AG);
            return new It(suiteelem());
        } else throw new Exception("AG of It cannot be reduce");
    }

    public SuiteItems suiteItem() throws Exception{
        if(this.reader.check(Sym.ITEM)) {
            reader.eat(Sym.ITEM);
            Item it = new Item(suiteelem());
            SuiteItems b = new SuiteItems(it, suiteItem());
            return b;
        }
        reader.eat(Sym.ENDENUM);
        return null;
    }

    public Enumeration enumerate() throws Exception {
        return new ConstructEnumeration(this.suiteItem());
    }
}