package jooq.custom.generator;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

public class JPrefixGeneratorStrategy extends DefaultGeneratorStrategy {

    @Override
    public String getJavaClassName(final Definition definition, final Mode mode) {
        if (mode == Mode.DEFAULT) {
            return "J" + super.getJavaClassName(definition, mode);
        }

        if (mode == Mode.DAO) {
            return super.getJavaClassName(definition, mode).replaceAll("Dao", "Repository");
        }
        return super.getJavaClassName(definition, mode);
    }
}
