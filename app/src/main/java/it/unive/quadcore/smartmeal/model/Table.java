package it.unive.quadcore.smartmeal.model;

import java.util.Objects;

public abstract class Table implements Comparable<Table>{ // TODO : Togliere l'abstract ?
    // possibile costruttore package-private chiamato da un'altra classe

    // eventuale array esterno di tavoli (classe che si occupa di istanziare i tavoli
    // disponibili nel locale)

    // TODO : si potrebbe fare intero
    private final String id ;

    protected Table(String id){
        this.id = id;
    }

    public String getId(){
        return id ;
    }

    // Comparo rispetto l'id (ordine lessicografico). Ordinamento naturale di Table
    public int compareTo(Table other){
        return id.compareTo(other.id) ;
    }

    // Uguaglianza rispetto id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(id, table.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Table{" +
                "id='" + id + '\'' +
                '}';
    }
}
