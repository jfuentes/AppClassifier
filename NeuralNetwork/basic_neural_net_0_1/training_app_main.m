function training_app_main()

clc;

data_sets = load_data('mushroom1');

experiment_count = 30;

for k=1:experiment_count
    fprintf('-------------------------\n');
    fprintf('Experiment %d\n', k);
    [weights, errors, training_time(k)] = train(data_sets);
    
    regression_error(k) = errors.test.regression;
    classification_error(k) = errors.test.classification;    
end
fprintf('-------------------------\n');
fprintf('Training Time\n');
fprintf('\t %g (mean)\n', mean(training_time));
fprintf('\t %g (std)\n', std(training_time));
fprintf('\t %g (max)\n', max(training_time));

fprintf('Regression Error\n');
fprintf('\t %g (mean)\n', mean(regression_error));
fprintf('\t %g (std)\n', std(regression_error));
fprintf('\t %g (max)\n', max(regression_error));

fprintf('Classification Error\n');
fprintf('\t %g (mean)\n', mean(classification_error));
fprintf('\t %g (std)\n', std(classification_error));
fprintf('\t %g (max)\n', max(classification_error));

fprintf('-------------------------\n');

fprintf('\t %g\t%g\t%g\n', mean(training_time), std(training_time), max(training_time));
fprintf('\t %g\t%g\t%g\n', mean(regression_error), std(regression_error), max(regression_error));
fprintf('\t %g\t%g\t%g\n', mean(classification_error), std(classification_error), max(classification_error));

