{:name       "bidding problem"
 :background []
 :start      [(bid 0)]
 :goal       [(bid 5)]
 :actions
             [(define-action post-new-bid (?number)
                             {:preconditions [(bid ?number)]
                              :additions     [(bid ($$sum 1 ?number))]
                              :deletions     [(bid ?number)]})]

 :expected-plans ([(post-new-bid 0)
                   (post-new-bid 1)
                   (post-new-bid 2)
                   (post-new-bid 3)
                   (post-new-bid 4)])}
