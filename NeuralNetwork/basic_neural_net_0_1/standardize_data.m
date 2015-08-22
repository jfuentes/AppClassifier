function new_inputs = standardise_data(inputs)

[sample_count, cols] = size(inputs);

mean_data = mean(inputs);

new_inputs = zeros(sample_count, cols);

for k=1:sample_count
    new_inputs(k, :) = inputs(k, :) - mean_data;
end

min_data = min(new_inputs);
max_data = max(new_inputs);

divisors = max(abs(min_data), abs(max_data));

for k=1:sample_count
    new_inputs(k, :) = new_inputs(k, :) ./ divisors * .475 + 0.5;
end