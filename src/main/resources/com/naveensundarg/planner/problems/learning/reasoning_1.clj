{:name       "learning"
 :background []
 :start      [(holds c1 a null)]

 :actions    [(define-action resolve [?c1 ?c2]
                {:preconditions [(holds ?c1 ?u null)
                                 (holds ?c2 (not ?u) ?w)]
                 :additions     [(holds (comb ?c1 ?c2) null ?w)]
                 :deletions     []})


              (define-action resolve [?c1 ?c2]
                {:preconditions [(holds ?c1 null ?u )
                                 (holds ?c2 ?w (not ?u) )]
                 :additions     [(holds (comb ?c1 ?c2)  ?w null)]
                 :deletions     []})
              ]


 :goal [(holds ?id null null)]
 }