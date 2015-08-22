function [weights, errors, epoch_count] = train(data_sets)

if nargin == 0
    clc;
    data_sets = load_data('iris1');
    
    max_data = -1;
    
    if max_data > 0
        data_sets.training.inputs = data_sets.training.inputs(1:max_data,:);
        data_sets.training.outputs = data_sets.training.outputs(1:max_data,:);
        data_sets.training.classes = data_sets.training.classes(1:max_data);
        data_sets.training.bias = data_sets.training.bias(1:max_data);
        data_sets.training.count = max_data;
    end
    
    train(data_sets);
    
    return;
end

plot_data = true;
max_weight = 1/2;
max_iterations = 500;
eta = .1;
validation_stop_threshold = .1;

weights = initialise_weights(max_weight, data_sets.input_count + 1, data_sets.output_count);

epoch_count = 1;

while true
    
    weights = update_backpropagation(...
        data_sets.training.inputs,...
        weights,...
        data_sets.training.bias,...
        eta,...
        data_sets.training.outputs);    
       
        [training_regression_error(epoch_count), ...
            training_classification_error(epoch_count)] = ...
        eval_network(...
            data_sets.training,...
            weights);

        [validation_regression_error(epoch_count), ...
            validation_classification_error(epoch_count)] = ...
        eval_network(...
            data_sets.validation,...
            weights);

        [test_regression_error(epoch_count), ...
            test_classification_error(epoch_count)] = ...
        eval_network(...
            data_sets.test,...
            weights);
            
        if mod(epoch_count, 10) == 0
            fprintf('\tEpochs: %g', epoch_count);
            fprintf('\tTraining: %g (%g)\n', training_regression_error(epoch_count), training_classification_error(epoch_count));
            fprintf('\tValidation: %g (%g)\n', validation_regression_error(epoch_count), validation_classification_error(epoch_count));
            fprintf('\tTest: %g (%g)\n', test_regression_error(epoch_count), test_classification_error(epoch_count));
            fprintf('\n');            
        end
        
    %loop maintenance    
    if (validation_regression_error(epoch_count)) < validation_stop_threshold || (epoch_count >= max_iterations)
        break;
    end
    
    epoch_count = epoch_count + 1;  
end

[errors.training.regression, ...
    errors.training.classification] = ...
eval_network(...
    data_sets.training,...
    weights);

[errors.validation.regression, ...
    errors.validation.classification] = ...
eval_network(...
    data_sets.validation,...
    weights);

[errors.test.regression, ...
    errors.test.classification] = ...
eval_network(...
    data_sets.test,...
    weights);

fprintf('Training: %g (%g)\n', errors.training.regression, errors.training.classification);
fprintf('Validation: %g (%g)\n',errors.validation.regression, errors.validation.classification);
fprintf('Test: %g (%g)\n', errors.test.regression, errors.test.classification);

if plot_data
    %% Create figure
    figure1 = figure('PaperPosition',[0.6345 6.345 20.3 15.23],'PaperSize',[20.98 29.68]);

    %% Create axes
    plot1 = plot([...
        training_regression_error; ...
        validation_regression_error;...
        test_regression_error;...    
        training_classification_error; ...
        validation_classification_error;...
        test_classification_error;
        ]');

    title('Error vs. Learning Time');
    xlabel('Epochs');
    ylabel('Average Error');
		
    set(plot1(1),'Color',[1 0 0], 'LineStyle','-');
    set(plot1(2),'Color',[0 .7 .7], 'LineStyle','-'); 
    set(plot1(3),'Color',[.8 .9 0], 'LineStyle','-');

    set(plot1(4),'Color',[1 0 0], 'LineStyle',':');
    set(plot1(5),'Color',[0 .7 .7], 'LineStyle',':');
    set(plot1(6),'Color',[.8 .9 0], 'LineStyle',':');

    legend1 = legend({'Training', 'Training Classification','Validation', 'Validation Classification', 'Test', 'Test Classification'});
end
