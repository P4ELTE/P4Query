
% if goal == Goal.best:

R{"best_case"}min=? [ F op=DONE ] 

% elif goal == Goal.avg_best:

R{"best_case"}=? [ F op=DONE ] 

% elif goal == Goal.avg:

R{"avg_case"}=? [ F op=DONE ] 

% elif goal == Goal.avg_worst:

R{"worst_case"}=? [ F op=DONE ] 

% elif goal == Goal.worst:

R{"worst_case"}max=? [ F op=DONE ] 

% endif


<%doc> Unused:
R{"best_case"}max=? [ F op=DONE ] 
R{"worst_case"}min=? [ F op=DONE ] 
R{"avg_case"}min=? [ F op=DONE ] 
R{"avg_case"}max=? [ F op=DONE ] 
</%doc>


