using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Drawer : MonoBehaviour
{

    private double maximumPullZPosition = 0.4;
    private double minimumPullZPosition = 0.2;

    [Header( "Grasping Properties" )]
	public float graspingRadius = 0.1f;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {

    }

    public void PullDrawer(Vector3 position) {
        //Vector3 distance = Vector3.Distance( this.transform.position, position );
        //if (position.z <= maximumPullZPosition && position.z >= minimumPullZPosition) {
            transform.localPosition = new Vector3(transform.localPosition.x, transform.localPosition.y, transform.localPosition.z);
            Debug.LogWarningFormat( "Pulled Drawer" );
        //}
    }

    public float get_grasping_radius() {
        return graspingRadius;
    }
}
