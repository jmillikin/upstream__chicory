package com.dylibso.chicory.runtime;

import static com.dylibso.chicory.wasm.types.Value.REF_NULL_VALUE;

import com.dylibso.chicory.wasm.exceptions.ChicoryException;
import com.dylibso.chicory.wasm.exceptions.UninstantiableException;
import com.dylibso.chicory.wasm.types.Limits;
import com.dylibso.chicory.wasm.types.Table;
import com.dylibso.chicory.wasm.types.Value;
import com.dylibso.chicory.wasm.types.ValueType;
import java.util.Arrays;

public class TableInstance {

    private Table table;
    private Instance[] instances;
    private int[] refs;

    public TableInstance(Table table) {
        this.table = table;
        this.instances = new Instance[(int) table.limits().min()];
        refs = new int[(int) table.limits().min()];
        Arrays.fill(refs, REF_NULL_VALUE);
    }

    public int size() {
        return refs.length;
    }

    public ValueType elementType() {
        return table.elementType();
    }

    public Limits limits() {
        return table.limits();
    }

    public int grow(int size, int value, Instance instance) {
        var oldSize = refs.length;
        var targetSize = oldSize + size;
        if (size < 0 || targetSize > limits().max()) {
            return -1;
        }
        var newRefs = Arrays.copyOf(refs, targetSize);
        Arrays.fill(newRefs, oldSize, targetSize, value);
        var newInstances = Arrays.copyOf(instances, targetSize);
        Arrays.fill(newInstances, oldSize, targetSize, instance);
        refs = newRefs;
        instances = newInstances;
        return oldSize;
    }

    public Value ref(int index) {
        int res;
        try {
            res = this.refs[index];
        } catch (IndexOutOfBoundsException e) {
            throw new ChicoryException("undefined element", e);
        }
        if (this.elementType() == ValueType.FuncRef) {
            return Value.funcRef(res);
        } else {
            return Value.externRef(res);
        }
    }

    public void setRef(int index, int value, Instance instance) {
        try {
            this.refs[index] = value;
            this.instances[index] = instance;
        } catch (IndexOutOfBoundsException e) {
            throw new UninstantiableException("out of bounds table access", e);
        }
    }

    public Instance instance(int index) {
        return instances[index];
    }

    public void reset() {
        for (int i = 0; i < refs.length; i++) {
            this.refs[i] = REF_NULL_VALUE;
        }
    }
}
