function data_sets = load_data(data_file_name)

data_sets = load(data_file_name);

data_sets.training.bias = ones(data_sets.training_count, 1);
data_sets.validation.bias = ones(data_sets.validation_count, 1);
data_sets.test.bias = ones(data_sets.test_count, 1);

data_sets.training.count = data_sets.training_count;
data_sets.validation.count = data_sets.validation_count;
data_sets.test.count = data_sets.test_count;