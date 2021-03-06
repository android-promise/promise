package promise.data.db.query.projection;

import promise.data.db.Utils;
import promise.model.List;
import promise.model.function.MapFunction;

public class ConstantProjection extends Projection {
  private Object constant;

  public ConstantProjection(Object constant) {
    this.constant = constant;
  }

  @Override
  public String build() {
    if (constant != null) return "?";
    else return "NULL";
  }

  @Override
  public List<String> buildParameters() {
    if (constant != null) {
      List<Object> ret = new List<Object>();
      ret.add(constant);

      return ret.map(
          new MapFunction<String, Object>() {
            @Override
            public String from(Object o) {
              return String.valueOf(o);
            }
          });
    } else {
      return Utils.EMPTY_LIST.map(
          new MapFunction<String, Object>() {
            @Override
            public String from(Object o) {
              return String.valueOf(o);
            }
          });
    }
  }
}
