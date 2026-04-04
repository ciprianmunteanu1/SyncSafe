import os
import re

directory = "composeApp/src/commonMain/kotlin/org/example/project/model"
for filename in os.listdir(directory):
    if filename.endswith(".kt"):
        filepath = os.path.join(directory, filename)
        with open(filepath, 'r') as file:
            content = file.read()
        
        if "@Serializable" not in content:
            # Add import
            if "import kotlinx.serialization.Serializable" not in content:
                content = re.sub(r'package (.*)\n', r'package \1\n\nimport kotlinx.serialization.Serializable\n', content)
            
            # Add @Serializable to classes and enums
            content = re.sub(r'(data class|enum class|class) ([A-Z]\w*)', r'@Serializable\n\1 \2', content)
            
            with open(filepath, 'w') as file:
                file.write(content)
        
        print(f"Processed {filename}")
