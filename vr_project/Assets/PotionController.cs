using UnityEngine;

class PotionController: MonoBehaviour
{
private float reduceFactor = 0.2f;
    public void OnTriggerEnter(Collider player)
    {
        Destroy(gameObject);
        player.transform.localScale = new Vector3(reduceFactor, reduceFactor, reduceFactor);
    }
}