package ${package.name};

import org.switchyard.component.bean.Service;

@Service(${service.name}.class) 
public class ${service.name}Bean implements org.switchyard.issues.${service.name} {

    @Override
    public void process(String content) {
        // Add processing logic here
    }

}
