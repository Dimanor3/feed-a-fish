#!/bin/bash
# Iterate through all the '*.jpg' files
for file_name in *.jpg; do
    # If there are no results, it returns '*.jpg'
    if [ "$file_name" == '*.jpg' ]; then
        continue
    fi 
    
    gimp -i -b "(single-makeafishcropper \"$file_name\")" -b "(gimp-quit 0)"
done
