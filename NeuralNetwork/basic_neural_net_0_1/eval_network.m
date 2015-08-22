function [regression_error, classification_error] = eval_network(data_set, weights)

[outputs, net] = feedforward(data_set.inputs, weights, data_set.bias);

[rows, output_count] = size(weights);

regression_error = sum(sum((outputs - data_set.outputs) .^2)) / (data_set.count * output_count);
classes = classes_from_outputs(outputs);
classification_error = sum(classes ~= data_set.classes) / data_set.count;
