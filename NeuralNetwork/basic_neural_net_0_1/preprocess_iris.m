function preprocess_iris()

fprintf('\tPreprocessing iris...\n');

sample_count = 150;
input_count = 4;
output_count = 3;

training_count = round(sample_count / 2);
test_count = round(sample_count / 4);
validation_count = sample_count - test_count - training_count;

for data_set_index = 1:3
    fid = fopen('iris.data','r');

    iris_data = fscanf(fid,'%g,%g,%g,%g,%g', [5, sample_count]);
    status = fclose(fid);

    iris_data = iris_data'; %transpose for visual convenience

    inputs = iris_data(:, 1:4);
    classes = iris_data(:, 5);

    inputs = standardise_data(inputs);
    outputs = outputs_from_classes(classes);

    class_perm = randperm(sample_count);

    for k = 1:sample_count
        p = class_perm(k);
        shuffled_inputs(k,:) = inputs(p,:);
        shuffled_outputs(k,:) = outputs(p,:);
        shuffled_classes(k) = classes(p);
    end

    training.inputs = shuffled_inputs(1:training_count,:);
    training.outputs = shuffled_outputs(1:training_count,:);
    training.classes = shuffled_classes(1:training_count)';

    validation_start = training_count + 1;
    validation_end = training_count + validation_count;

    validation.inputs = shuffled_inputs(validation_start:validation_end,:);
    validation.outputs = shuffled_outputs(validation_start:validation_end,:);
    validation.classes = shuffled_classes(validation_start:validation_end)';

    test_start = validation_end + 1;

    test.inputs = shuffled_inputs(test_start:end,:);
    test.outputs = shuffled_outputs(test_start:end,:);
    test.classes = shuffled_classes(test_start:end)';
    data_set_name = sprintf('iris%d', data_set_index);


    save(data_set_name, 'training', 'validation', 'test',...
    'input_count', 'output_count', ...
                'training_count', 'validation_count', 'test_count');
end