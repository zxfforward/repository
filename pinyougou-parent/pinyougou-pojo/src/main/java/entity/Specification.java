package entity;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * Created by crowndint on 2018/10/14.
 */
public class Specification implements Serializable {

    private TbSpecification spec;

    private List<TbSpecificationOption> specOptions;

    public TbSpecification getSpec() {
        return spec;
    }

    public void setSpec(TbSpecification spec) {
        this.spec = spec;
    }

    public List<TbSpecificationOption> getSpecOptions() {
        return specOptions;
    }

    public void setSpecOptions(List<TbSpecificationOption> specOptions) {
        this.specOptions = specOptions;
    }
}
