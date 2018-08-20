import java.util.ArrayList;
import java.util.List;

class Trade {
    List<Player> players1 = new ArrayList<>(), players2 = new ArrayList<>();

    Trade(List<Player> players1, List<Player> players2) {
        this.players1 = players1;
        this.players2 = players2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trade trade = (Trade) o;

        if (players1 != null ? !players1.equals(trade.players1) : trade.players1 != null) return false;
        return players2 != null ? players2.equals(trade.players2) : trade.players2 == null;
    }

    @Override
    public int hashCode() {
        int result = players1 != null ? players1.hashCode() : 0;
        result = 31 * result + (players2 != null ? players2.hashCode() : 0);
        return result;
    }

    @Override

    public String toString() {
        return "Trade{" +
                "players1=" + players1 +
                ", players2=" + players2 +
                '}';
    }
}
