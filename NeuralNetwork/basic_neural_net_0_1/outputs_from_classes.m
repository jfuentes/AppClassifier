function outputs = make_outputs_from_classes(classes)

sample_count = length(classes);

outputs = zeros(sample_count, max(classes));

for k = 1:sample_count
    outputs(k, classes(k)) = 1;
end