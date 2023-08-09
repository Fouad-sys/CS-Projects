using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Magnetic : ObjectAnchor
{

    public Vector3 targetPosition;
    public bool dragged;

    private Transform trans;

    // Start is called before the first frame update
    void Start()
    {
        trans = transform;
        dragged = false;
        targetPosition = trans.position;
    }

    // Update is called once per frame
    void Update()
    {
        if (dragged) {
            trans.position = Vector3.Lerp(trans.position, targetPosition, Time.deltaTime * 1.5f);
            Debug.LogWarningFormat("In Dragged Lerp");

            if (Mathf.Abs(targetPosition.x - trans.position.x) < 0.005) {
                Debug.LogWarningFormat( "Magnet lerp done" );
                trans.position = targetPosition;
                dragged = false;
            }
        }
    }

    public void drag(Vector3 tPosition) {
        targetPosition = tPosition;
        dragged = true;
    }
}