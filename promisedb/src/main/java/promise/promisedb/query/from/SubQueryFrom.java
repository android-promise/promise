package promise.promisedb.query.from;

import promise.model.List;
import promise.promisedb.Utils;
import promise.promisedb.query.QueryBuilder;

public class SubQueryFrom extends AliasableFrom<SubQueryFrom> {
  private QueryBuilder subQuery;

  public SubQueryFrom(QueryBuilder subQuery) {
    this.subQuery = subQuery;
  }

  @Override
  public String build() {
    String ret = (subQuery != null ? "(" + subQuery.build() + ")" : "");

    if (!Utils.isNullOrWhiteSpace(alias)) ret = ret + " AS " + alias;

    return ret;
  }

  @Override
  public List<String> buildParameters() {
    if (subQuery != null) return List.Companion.fromArray(subQuery.buildParameters());
    else
      return Utils.EMPTY_LIST.map(
          String::valueOf);
  }
}
