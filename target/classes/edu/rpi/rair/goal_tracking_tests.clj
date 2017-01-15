{:name       "Sim"
 :background []
 :start      [(In self room1)
              (In commander room2)
              (In prisoner room1)
              (Open (door room2))
              (not (Open (door room1))) ]

 :actions     [(define-action open-door [?room]
                             {:preconditions [(not (Open (door ?room)))]
                              :additions [(Open (door ?room))]
                              :deletions [(not (Open (door ?room)))]})



              (define-action accompany-from-to [?thing ?room1 ?room2]
                             {:preconditions [(In self ?room1)
                                              (In ?thing ?room1)
                                              (Open (door ?room1))
                                              (Open (door ?room2))]

                              :additions     [(In ?thing ?room2)
                                              (In ?self ?room2)]
                              :deletions     [(In ?thing ?room1)
                                              (In self ?room1)]})]

 :expected-plans ([(open-door room1)
                   (accompany-from-to prisoner room1 room2)])

 }