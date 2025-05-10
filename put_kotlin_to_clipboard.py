import pyperclip
from pathlib import Path

combined_kotlin_contents = '\n'.join(
    file_path.read_text() for file_path in Path('.').rglob('*.kt')
)
pyperclip.copy(combined_kotlin_contents)
print('✅ Successfully copied all Kotlin file contents to clipboard.')
