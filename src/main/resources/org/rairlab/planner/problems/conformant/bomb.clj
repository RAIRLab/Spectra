; Original problem from Joerg Hoffmann and Ronen Brafman
{:name "Bomb-In-The-Toilet"
    :background [
        ; Setting object types
        (bomb bomb1)
        (bomb bomb2)
        (bomb bomb3)
        (bomb bomb4)
        (bomb bomb5)
        (toilet toilet1)
    ]


    :actions [
        (define-action dunk [?bomb ?toilet] {
            :preconditions [
                ; Type restriction
                (bomb ?bomb)
                (toilet ?toilet)
                ; Preconditions
                (not (clogged ?toilet))
            ]
            :additions [
                (not (armed ?bomb))
                (clogged ?toilet)
            ]
            :deletions [
                (armed ?bomb)
                (not (clogged ?toilet))
            ]
        })

        (define-action flush [?toilet] {
            :preconditions [ (toilet ?toilet) ]
            :additions [ (not (clogged ?toilet)) ]
            :deletions [ (clogged ?toilet) ]
        })
    ]
    :start [
        ; Unknown facts don't need to be stated 
        ; since we don't assume closed world assumption.
        ; (unknown (armed bomb1))
        ; (unknown (armed bomb2))
        ; (unknown (armed bomb3))
        ; (unknown (armed bomb4))
        ; (unknown (armed bomb5))
    ]
    :goal [
        (not (armed bomb1))
        (not (armed bomb2))
        (not (armed bomb3))
        (not (armed bomb4))
        (not (armed bomb5))
    ]

}
