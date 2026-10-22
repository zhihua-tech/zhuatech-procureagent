/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.procureagent.workbench;

import static cn.zhuatech.procureagent.workbench.WorkbenchInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);
    keys(r, "currency", "items", "quotes");
    String currency = text(r, "currency", 3);
    if (!currency.matches("[A-Z]{3}")) throw new IllegalArgumentException("币种格式错误");
    Map<String, BigDecimal> quantities = new LinkedHashMap<>();
    for (var item : rows(r, "items", 1, 100)) {
      keys(item, "id", "quantity");
      String sku = id(item, "id");
      if (quantities.putIfAbsent(sku, decimal(item, "quantity", 0.000001, 1e6)) != null)
        throw new IllegalArgumentException("需求物料重复");
    }
    Set<String> suppliers = new HashSet<>();
    List<Map<String, Object>> out = new ArrayList<>();
    for (var q : rows(r, "quotes", 1, 50)) {
      keys(q, "supplier", "currency", "freight", "taxRate", "lines");
      String supplier = id(q, "supplier");
      unique(suppliers, supplier);
      if (!currency.equals(text(q, "currency", 3))) throw new IllegalArgumentException("不能比较不同币种");
      BigDecimal subtotal = BigDecimal.ZERO;
      Set<String> included = new HashSet<>();
      for (var line : rows(q, "lines", 0, 100)) {
        keys(line, "itemId", "unitPrice");
        String sku = id(line, "itemId");
        unique(included, sku);
        if (!quantities.containsKey(sku)) throw new IllegalArgumentException("报价包含未知物料");
        subtotal = subtotal.add(quantities.get(sku).multiply(decimal(line, "unitPrice", 0, 1e9)));
      }
      var missing = quantities.keySet().stream().filter(s -> !included.contains(s)).toList();
      BigDecimal base = subtotal.add(money(q, "freight")), rate = decimal(q, "taxRate", 0, 1);
      BigDecimal total = base.multiply(BigDecimal.ONE.add(rate)).setScale(2, RoundingMode.HALF_UP);
      out.add(
          Map.of(
              "supplier",
              supplier,
              "eligible",
              missing.isEmpty(),
              "missingItems",
              missing,
              "total",
              total,
              "currency",
              currency));
    }
    out.sort(
        Comparator.<Map<String, Object>, Boolean>comparing(x -> !(Boolean) x.get("eligible"))
            .thenComparing(x -> (BigDecimal) x.get("total"))
            .thenComparing(x -> x.get("supplier").toString()));
    return Map.of("quotes", out, "automaticAward", false);
  }
}
